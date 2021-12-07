package com.graphic.key.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.graphic.key.data.RegistrationData
import com.graphic.key.task.DataSender

class RegistrationViewModel : ViewModel() {

    fun sendRegistrationData(url: String, registrationData: RegistrationData): LiveData<String> {
        return liveData {
            val data = DataSender(url).send(registrationData)
            emit(data)
        }
    }
}