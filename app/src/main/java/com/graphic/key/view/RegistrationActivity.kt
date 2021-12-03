package com.graphic.key.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.graphic.key.R
import com.graphic.key.data.RegistrationData
import com.graphic.key.task.DataSender
import java.lang.Integer.parseInt
import java.lang.ref.WeakReference
import java.util.*

class RegistrationActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reg_layout)

        if (getUid(this) != null) {
            val intent = Intent(this, HealthTestActivity::class.java)
            startActivity(intent)
        }

        val emailTextView = findViewById<TextView>(R.id.emailInput)
        val weightTextView = findViewById<TextView>(R.id.weightInput)
        val heightTextView = findViewById<TextView>(R.id.heightInput)
        val ageTextView = findViewById<TextView>(R.id.ageInput)
        val sexRadioGroup = findViewById<RadioGroup>(R.id.sexRadioGroup)
        val serverAddress = findViewById<TextView>(R.id.serverAddress)
        serverAddress.text = getString(R.string.server_address)

        val regButton = findViewById<Button>(R.id.registrationButton)

        regButton.setOnClickListener {
            val emptyFields = arrayListOf<String>()
            val email = emailTextView.text.toString()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email заполнен некорректно", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (weightTextView.text.isEmpty()) emptyFields.add("Вес")
            if (heightTextView.text.isEmpty()) emptyFields.add("Рост")
            if (ageTextView.text.isEmpty()) emptyFields.add("Возраст")
            if (serverAddress.text.isEmpty()) emptyFields.add("Адрес сервера")

            if (emptyFields.isNotEmpty()) {
                Toast.makeText(this, String.format("Не заполнены следующие поля: %s",
                        emptyFields.joinToString(", ")), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val sex = if (sexRadioGroup.checkedRadioButtonId == R.id.femaleRadioButton) "female" else "male"

            val uid = generateUID(this)
            val regData = RegistrationData(uid,
                    email,
                    parseInt(weightTextView.text.toString()),
                    parseInt(heightTextView.text.toString()),
                    parseInt(ageTextView.text.toString()),
                    sex)

            val serverUrl = serverAddress.text.toString()
            saveServerUrl(serverUrl)

            val registrationUrl = serverUrl + "/" + this.getString(R.string.registrationUrl)
            val task = DataSender(WeakReference(this.applicationContext), registrationUrl)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, regData)

            val intent = Intent(this, HealthTestActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }
    }

    private fun saveServerUrl(url: String) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("SERVER_URL", url).apply()
    }

    companion object Uid {
        fun getUid(context: Context): String? {
            return PreferenceManager.getDefaultSharedPreferences(context).getString("UID", null)
        }

        fun generateUID(context: Context): String {
            val uid = UUID.randomUUID().toString()
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("UID", uid).apply()
            return uid
        }
    }


}