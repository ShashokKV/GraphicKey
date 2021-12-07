package com.graphic.key.data.model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphic.key.data.DrawData
import com.graphic.key.data.UserInputData
import com.graphic.key.task.DataSender
import kotlinx.coroutines.launch

class KeyViewModel : ViewModel() {
    private var drawDataList: MutableList<DrawData> = arrayListOf()

    fun getDrawDataList(): MutableList<DrawData> {
        return drawDataList
    }

    fun addToDrawData(drawData: DrawData) {
        drawDataList.add(drawData)
    }

    fun sendDrawData(url: String, userInputData: UserInputData): String {
        var result = ""
        viewModelScope.launch {
            result = DataSender(url).send(userInputData)
        }
        return result
    }

    override fun onCleared() {
        drawDataList = arrayListOf()
    }
}