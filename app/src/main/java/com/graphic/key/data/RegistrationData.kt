package com.graphic.key.data

import java.io.Serializable

data class RegistrationData(
    val uid: String,
    val email: String,
    val weight: Int,
    val height: Int,
    val age: Int,
    val sex: String
) : Serializable