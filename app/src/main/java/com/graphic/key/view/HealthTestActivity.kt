package com.graphic.key.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import com.graphic.key.R
import com.graphic.key.data.HealthTestData

class HealthTestActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_layout)

        val healthRadioGroup = findViewById<RadioGroup>(R.id.radioGroupHealth)
        val physicalActivityRadioGroup = findViewById<RadioGroup>(R.id.radioGroupPhysicalActivity)
        val tirednessRadioGroup = findViewById<RadioGroup>(R.id.radioGroupTiredness)
        val alcoholRadioGroup = findViewById<RadioGroup>(R.id.radioGroupAlcohol)

        val testConfirmButton = findViewById<Button>(R.id.buttonTestEnter)
        testConfirmButton.setOnClickListener {
            val health = when(healthRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonExcellent->"excellent"
                R.id.radioButtonSatisfactory->"satisfactory"
                R.id.radioButtonReduced->"reduced"
                R.id.radioButtonVeryBad->"bad"
                else -> "bad"
            }

            val physicalActivity = when(physicalActivityRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonPhysicalActivityNo -> "no"
                else -> "yes"
            }

            val tiredness = when(tirednessRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonTirednessNo -> "no"
                else -> "yes"
            }

            val alcohol = when(alcoholRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonAlcoholNo -> "no"
                R.id.radioButtonAlcoholYes -> "yes"
                else -> "hangover"
            }

            val healthTestData = HealthTestData(health, physicalActivity, tiredness, alcohol)

            val uid = RegistrationActivity.getUid(this)

            val intent = Intent(this, KeyActivity::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("healthTestData", healthTestData)
            startActivity(intent)
        }
    }
}
