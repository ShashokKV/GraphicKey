package com.graphic.key.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphic.key.data.RegistrationData
import com.graphic.key.task.DataSender
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {

    fun sendRegistrationData(url: String, registrationData: RegistrationData): String {
        var result =""
        viewModelScope.launch {
            result = DataSender(url).send(registrationData)
        }
        return result
    }
}