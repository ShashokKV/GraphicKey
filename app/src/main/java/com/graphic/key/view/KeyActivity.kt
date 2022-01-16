package com.graphic.key.view


import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.graphic.key.R
import com.graphic.key.data.DrawData
import com.graphic.key.data.HealthTestData
import com.graphic.key.data.KeyData
import com.graphic.key.data.UserInputData
import com.graphic.key.data.model.KeyViewModel
import com.graphic.key.task.DataSender.Companion.SUCCESS
import kotlin.math.pow
import kotlin.math.roundToInt


class KeyActivity : ComponentActivity() {
    private lateinit var buttons: List<RoundButton>
    private var keyButtons: MutableList<RoundButton> = mutableListOf()
    private var currentKey: Int = 1
    private lateinit var imageView: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var detectInput = true
    private var attempts = 0

    private var stopX: Float = 0f
    private var stopY: Float = 0f

    private var startTimestamp: Long = 0
    private var firstTouchTime: Long = 0
    private var timerStarted = false
    private val keyViewModel: KeyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.key_layout)

        imageView = findViewById<View>(R.id.draw_pad) as ImageView

        val dw: Int
        val dh: Int

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            dw = bounds.width()
            dh = bounds.height()
        } else {
            @Suppress("DEPRECATION")
            val currentDisplay = windowManager.defaultDisplay
            val point = Point()
            @Suppress("DEPRECATION")
            currentDisplay.getSize(point)
            dw = point.x
            dh = point.y
        }

        bitmap = Bitmap.createBitmap(dw, dh, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE

        paint.strokeWidth = 15.0f
        imageView.layoutParams.height = canvas.height
        imageView.layoutParams.width = canvas.width
        imageView.requestLayout()
        imageView.setImageBitmap(bitmap)
    }

    override fun onResume() {
        super.onResume()
        initButtons()
        generateKey()
        startTimer()
    }

    private fun startTimer() {
        timerStarted = true
        startTimestamp = System.currentTimeMillis()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }
        if (timerStarted) updateStartTimestamp()
        onTouch(event)
        return super.dispatchTouchEvent(event)
    }

    private fun updateStartTimestamp() {
        timerStarted = false
        firstTouchTime = System.currentTimeMillis() - startTimestamp
    }

    private fun onTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                draw(event)
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_UP -> {
                draw(event)
                if (detectInput) keyIncorrect()
                reset()
                return
            }
        }

        if (!detectInput) return

        keyViewModel.addToDrawData(
            DrawData(
                event.x,
                event.y,
                System.currentTimeMillis() - startTimestamp
            )
        )
        val touchedButton =
            buttons.filter { button -> button.isTouched(event.x, event.y) }.getOrNull(0)
        if (touchedButton != null) {
            if (touchedButton.key != null) {
                if (touchedButton.key == currentKey) {
                    touchedButton.setGreenColor()
                    touchedButton.setTimeToTouch()
                    if (currentKey == keyButtons.size) {
                        keyCorrect()
                    } else {
                        currentKey++
                    }
                } else if (touchedButton.key != currentKey - 1) {
                    touchedButton.setRedColor()
                    keyIncorrect()
                }
            } else {
                touchedButton.setRedColor()
                keyIncorrect()
            }
        }
    }

    private fun draw(event: MotionEvent) {
        stopX = event.x
        stopY = event.y
        canvas.drawLine(startX, startY, stopX, stopY, paint)
        imageView.invalidate()
    }

    private fun keyCorrect() {
        detectInput = false

        val intent = intent
        val uid = RegistrationActivity.getUid(this) ?: RegistrationActivity.generateUID(this)
        val testId = getAndIncrementTestId()
        val healthTest = intent.getSerializableExtra("healthTestData") as HealthTestData
        val userInputData = UserInputData(
            uid, testId, attempts, startTimestamp, firstTouchTime, healthTest,
            keyButtons.map { keyButton ->
                KeyData(
                    keyButton.key
                        ?: 0, keyButton.touchTimestamp, keyButton.buttonX, keyButton.buttonY
                )
            },
            keyViewModel.getDrawDataList()
        )

        val url = getString(R.string.server_address) + "/" + this.getString(R.string.dataUrl)

        keyViewModel.sendDrawData(url, userInputData)
            .observe(this) { result ->
                if (result != SUCCESS) {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }
            }

        attempts = 0
    }

    private fun getAndIncrementTestId(): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val testId = prefs.getInt("testId", 1)
        prefs.edit { putInt("testId", testId + 1) }
        return testId
    }

    private fun keyIncorrect() {
        detectInput = false
        attempts++
    }

    private fun reset() {
        keyViewModel.resetDrawData()
        buttons.forEach { button ->
            button.setDefaultColor()
            button.setText("")
            button.key = null
        }

        bitmap.eraseColor(Color.TRANSPARENT)
        imageView.invalidate()

        detectInput = true

        generateKey()
        startTimer()
    }

    private fun initButtons() {
        val button1 = RoundButton(findViewById(R.id.button1))
        val button2 = RoundButton(findViewById(R.id.button2))
        val button3 = RoundButton(findViewById(R.id.button3))
        val button4 = RoundButton(findViewById(R.id.button4))
        val button5 = RoundButton(findViewById(R.id.button5))
        val button6 = RoundButton(findViewById(R.id.button6))
        val button7 = RoundButton(findViewById(R.id.button7))
        val button8 = RoundButton(findViewById(R.id.button8))
        val button9 = RoundButton(findViewById(R.id.button9))
        val button10 = RoundButton(findViewById(R.id.button10))
        val button11 = RoundButton(findViewById(R.id.button11))
        val button12 = RoundButton(findViewById(R.id.button12))

        button1.addNeighbors(listOf(button2, button4, button5))
        button2.addNeighbors(listOf(button1, button3, button4, button5, button6))
        button3.addNeighbors(listOf(button2, button5, button6))
        button4.addNeighbors(listOf(button1, button2, button5, button7, button8))
        button5.addNeighbors(
            listOf(
                button1,
                button2,
                button3,
                button4,
                button6,
                button7,
                button8,
                button9
            )
        )
        button6.addNeighbors(listOf(button2, button3, button5, button8, button9))
        button7.addNeighbors(listOf(button4, button5, button8, button10, button11))
        button8.addNeighbors(
            listOf(
                button4,
                button5,
                button6,
                button7,
                button9,
                button10,
                button11,
                button12
            )
        )
        button9.addNeighbors(listOf(button5, button6, button8, button11, button12))
        button10.addNeighbors(listOf(button7, button8, button11))
        button11.addNeighbors(listOf(button7, button8, button9, button10, button12))
        button12.addNeighbors(listOf(button8, button9, button11))

        buttons = listOf(
            button1, button2, button3,
            button4, button5, button6,
            button7, button8, button9,
            button10, button11, button12
        )
    }

    private fun generateKey() {
        keyButtons.clear()
        var neighbors = buttons
        currentKey = 1

        for (i in 1..KEYS_TO_GENERATE) {
            var keyButton = neighbors[randomInt(neighbors.size - 1)]
            while (keyButtons.contains(keyButton)) {
                keyButton = neighbors[randomInt(neighbors.size - 1)]
            }
            keyButton.key = i
            keyButton.setText(i.toString())
            neighbors = keyButton.neighbors
            keyButtons.add(keyButton)
        }
    }

    private fun randomInt(max: Int): Int {
        val min = 0
        return min + (Math.random() * (max - min)).roundToInt()
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class RoundButton(private val button: Button) {
        var buttonX = 0
        var buttonY = 0
        private var rad: Double = 0.0
        val neighbors: MutableList<RoundButton> = mutableListOf()
        var key: Int? = null
        var touchTimestamp: Long = 0

        fun setText(text: String) {
            button.text = text
        }

        fun setTimeToTouch() {
            touchTimestamp = System.currentTimeMillis() - startTimestamp
        }

        fun setGreenColor() {
            setColor(R.drawable.green_button_round)
        }

        fun setRedColor() {
            setColor(R.drawable.red_button_round)
        }

        fun setDefaultColor() {
            setColor(R.drawable.button_round)
        }

        private fun setColor(colorId: Int) {
            button.background = AppCompatResources.getDrawable(button.context, colorId)
        }

        fun isTouched(x: Float, y: Float): Boolean {
            if (rad == 0.0) rad = button.measuredWidth.toDouble() / 2
            if (buttonX == 0 || buttonY == 0) {
                val coordinates = intArrayOf(buttonX, buttonY)
                button.getLocationInWindow(coordinates)
                buttonX = coordinates[0] + rad.roundToInt()
                buttonY = coordinates[1] + rad.roundToInt()
            }
            return rad.pow(2.0) > (x - buttonX).toDouble().pow(2.0) + (y - buttonY).toDouble()
                .pow(2.0)
        }

        fun addNeighbors(buttons: List<RoundButton>) {
            neighbors.addAll(buttons)
        }
    }

    companion object {
        private const val KEYS_TO_GENERATE = 5
    }
}