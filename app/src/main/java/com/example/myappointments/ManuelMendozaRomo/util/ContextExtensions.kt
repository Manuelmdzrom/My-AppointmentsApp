package com.example.myappointments.ManuelMendozaRomo.util

import android.content.Context
import android.widget.Toast

fun Context.toast(mesage: CharSequence) =
    Toast.makeText(this,mesage,Toast.LENGTH_SHORT).show()