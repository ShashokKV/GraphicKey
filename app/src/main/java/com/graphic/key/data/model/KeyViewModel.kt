package com.graphic.key.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graphic.key.data.DrawData
import com.graphic.key.data.UserInputData

class KeyViewModel: ViewModel() {
    private lateinit var inputData: UserInputData
    private var drawDataList: MutableList<DrawData> = arrayListOf()


   fun addToDrawData(drawData: DrawData) {
       drawDataList.add(drawData)
   }


}