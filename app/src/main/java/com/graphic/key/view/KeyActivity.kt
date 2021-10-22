package com.graphic.key.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.*
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.graphic.key.R
import com.graphic.key.data.DrawData
import com.graphic.key.data.HealthTestData
import com.graphic.key.data.KeyData
import com.graphic.key.data.UserInputData
import com.graphic.key.task.SendDataTask
import java.lang.ref.WeakReference
import kotlin.math.pow
import kotlin.math.roundToInt


class KeyActivity : Activity() {
    private lateinit var buttons: List<RoundButton>
    private lateinit var keyButtons: Map<Int, RoundButton>
    private var currentKey: Int = 1
    private var drawData: MutableList<DrawData> = arrayListOf()
    private lateinit var imageView: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private var startx: Float = 0f
    private var starty: Float = 0f
    private var detectInput = true
    private var attempts = 0

    private var stopx: Float = 0f
    private var stopy: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.key_layout)

        imageView = findViewById<View>(R.id.draw_pad) as ImageView

        val currentDisplay = windowManager.defaultDisplay
        val point = Point()
        currentDisplay.getSize(point)
        val dw = point.x
        val dh = point.y

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
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }
        onTouch(event)
        return super.dispatchTouchEvent(event)
    }

    private fun onTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startx = event.x
                starty = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                draw(event)
                startx = event.x
                starty = event.y
            }
            MotionEvent.ACTION_UP -> {
                draw(event)
                if (detectInput) keyIncorrect()
                reset()
                return
            }
        }

        if (!detectInput) return

        drawData.add(DrawData(event.x, event.y, System.currentTimeMillis()))
        val touchedButton = buttons.filter { button -> button.isTouched(event.x, event.y) }.getOrNull(0)
        if (touchedButton != null) {
            if (touchedButton.key != null) {
                if (touchedButton.key == currentKey) {
                    touchedButton.setGreenColor()
                    if (currentKey == 3) {
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
        stopx = event.x
        stopy = event.y
        canvas.drawLine(startx, starty-100, stopx, stopy-100, paint)
        imageView.invalidate()

        Log.d("KEY", "canvas.height=${canvas.height}")
        Log.d("KEY", "canvas.width=${canvas.width}")
        Log.d("KEY", "imageView.height=${imageView.height}")
        Log.d("KEY", "imageView.width=${imageView.width}")
    }

    private fun keyCorrect() {
        detectInput = false
        Toast.makeText(this, "Введён правильный код", Toast.LENGTH_LONG).show()

        val intent = intent
        val uid = RegistrationActivity.getUid(this) ?: RegistrationActivity.generateUID(this)
        val healthTest = intent.getSerializableExtra("healthTestData") as HealthTestData
        val userInputData = UserInputData(uid, attempts, healthTest,
                keyButtons.map { keyButtons -> KeyData(keyButtons.key, keyButtons.value.buttonX, keyButtons.value.buttonY) },
                drawData)

        val url = this.getString(R.string.keyInputUrl)
        val task = SendDataTask(WeakReference(this.applicationContext), url)
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userInputData)
        attempts = 0
    }

    private fun keyIncorrect() {
        detectInput = false
        attempts++
        Toast.makeText(this, "Введён неправильный код", Toast.LENGTH_LONG).show()
    }

    private fun reset() {
        drawData = arrayListOf()
        buttons.forEach { button ->
            button.setDefaultColor()
            button.setText("")
            button.key = null
        }

        bitmap.eraseColor(Color.TRANSPARENT)
        imageView.invalidate()

        detectInput = true

        generateKey()
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

        button1.addNeighbors(listOf(button2, button4, button5))
        button2.addNeighbors(listOf(button1, button3, button4, button5, button6))
        button3.addNeighbors(listOf(button2, button5, button6))
        button4.addNeighbors(listOf(button1, button2, button5, button7, button8))
        button5.addNeighbors(listOf(button1, button2, button3, button4, button6, button7, button8, button9))
        button6.addNeighbors(listOf(button2, button3, button5, button8, button9))
        button7.addNeighbors(listOf(button4, button5, button8))
        button8.addNeighbors(listOf(button4, button5, button6, button7, button9))
        button9.addNeighbors(listOf(button5, button6, button8))

        buttons = listOf(button1, button2, button3, button4, button5, button6, button7, button8, button9)
    }

    private fun generateKey() {
        currentKey = 1
        var min = 0
        var max = 8
        val firstButton = buttons[randomInt(min, max)]
        firstButton.key = 1
        firstButton.setText("1")

        min = 0
        max = firstButton.neighbors.size - 1

        val secondButton = firstButton.neighbors[randomInt(min, max)]
        secondButton.key = 2
        secondButton.setText("2")
        max = secondButton.neighbors.size - 1

        var thirdButton = secondButton.neighbors[randomInt(min, max)]
        while (thirdButton == firstButton) {
            thirdButton = secondButton.neighbors[randomInt(min, max)]
        }

        thirdButton.key = 3
        thirdButton.setText("3")

        keyButtons = mapOf(Pair(1, firstButton), Pair(2, secondButton), Pair(3, thirdButton))
    }

    private fun randomInt(min: Int, max: Int): Int {
        return min + (Math.random() * (max - min)).roundToInt()
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class RoundButton(private val button: Button) {
        var buttonX = 0
        var buttonY = 0
        private var rad: Double = 0.0
        val neighbors: MutableList<RoundButton> = mutableListOf()
        var key: Int? = null

        fun setText(text: String) {
            button.text = text
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
            return rad.pow(2.0) > (x - buttonX).toDouble().pow(2.0) + (y - buttonY).toDouble().pow(2.0)
        }

        fun addNeighbors(buttons: List<RoundButton>) {
            neighbors.addAll(buttons)
        }


    }
}