package com.graphic.key.data

data class UserInputData(val uid: String, val attempts: Int, val healthTest: HealthTestData, val keyButtons: List<KeyData>, val drawData: MutableList<DrawData>)
