package com.example.myappointments.ManuelMendozaRomo.io.response

import com.example.myappointments.ManuelMendozaRomo.model.User

data class LoginResponse(val success: Boolean, val user: User, val jwt: String)