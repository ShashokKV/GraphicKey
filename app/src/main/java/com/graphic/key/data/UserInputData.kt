package com.graphic.key.data

data class UserInputData(
    val uid: String,
    val testId: Int,
    val attempts: Int,
    val startTimestamp: Long,
    val firstTouchTime: Long,
    val healthTest: HealthTestData,
    val keyButtons: List<KeyData>,
    val drawData: MutableList<DrawData>
)
