package com.graphic.key.data.model



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphic.key.data.DrawData
import com.graphic.key.data.UserInputData
import com.graphic.key.task.DataSender
import kotlinx.coroutines.launch

class KeyViewModel: ViewModel() {
    private var drawDataList: MutableList<DrawData> = arrayListOf()

    fun getDrawDataList(): MutableList<DrawData> {
        return drawDataList
    }

    fun addToDrawData(drawData: DrawData) {
       drawDataList.add(drawData)
   }

    fun sendDrawData(url: String, userInputData: UserInputData): LiveData<String> {
        val result = MutableLiveData<String>()
        viewModelScope.launch {
            result.value = DataSender(url).send(userInputData)
        }
        return result
    }

    override fun onCleared() {
        drawDataList = arrayListOf()
    }
}