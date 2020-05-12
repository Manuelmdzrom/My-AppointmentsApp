package com.example.myappointments.ManuelMendozaRomo

import com.example.myappointments.ManuelMendozaRomo.model.Doctor
import com.example.myappointments.ManuelMendozaRomo.model.Specialty
import com.google.gson.annotations.SerializedName

data class Appointment (
    val id: Int,
    val description: String,
    val type: String,
    val status: String,

    @SerializedName("schedule_date") val scheduleDate: String,
    @SerializedName("schedule_time_12") val scheduleTime: String,
    @SerializedName("created_at") val createAd: String,

    val specialty: Specialty,
    val doctor: Doctor
)