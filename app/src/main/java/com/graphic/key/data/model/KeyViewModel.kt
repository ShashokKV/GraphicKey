package com.graphic.key.data.model


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.graphic.key.data.DrawData
import com.graphic.key.data.UserInputData
import com.graphic.key.task.DataSender

class KeyViewModel : ViewModel() {
    private var drawDataList: MutableList<DrawData> = arrayListOf()

    fun getDrawDataList(): MutableList<DrawData> {
        return drawDataList
    }

    fun addToDrawData(drawData: DrawData) {
        drawDataList.add(drawData)
    }

    fun sendDrawData(url: String, userInputData: UserInputData): LiveData<String> {
        return liveData {
            val data = DataSender(url).send(userInputData)
            emit(data)
        }
    }

    fun resetDrawData() {
        drawDataList.clear()
    }

    override fun onCleared() {
        drawDataList = arrayListOf()
    }
}