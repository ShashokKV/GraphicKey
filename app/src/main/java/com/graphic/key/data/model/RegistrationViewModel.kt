package com.graphic.key.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphic.key.data.RegistrationData
import com.graphic.key.task.DataSender
import kotlinx.coroutines.launch

class RegistrationViewModel: ViewModel() {

    fun sendRegistrationData(url: String, registrationData: RegistrationData): LiveData<String> {
        val result = MutableLiveData<String>()
        viewModelScope.launch {
            result.value = DataSender(url).send(registrationData)
        }
        return result
    }
}