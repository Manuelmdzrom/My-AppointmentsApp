package com.example.myappointments.ManuelMendozaRomo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myappointments.ManuelMendozaRomo.PreferenceHelper
import com.example.myappointments.ManuelMendozaRomo.PreferenceHelper.set
import com.example.myappointments.ManuelMendozaRomo.R
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        btnCreateAppointment.setOnClickListener {
            val intent = Intent (this, CreatAppointmentActivity::class.java)
            startActivity(intent)
        }
        btnMyAppointments.setOnClickListener {
            val intent = Intent (this, AppointmentsActivity::class.java)
            startActivity(intent)
        }
        btnLogOut.setOnClickListener{
            clearSessionPreferences()
            val intent = Intent (this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun clearSessionPreferences(){
        /*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", false)
        editor.apply()
         */
        val preferences =
            PreferenceHelper.defaultPrefs(
                this
            )
        preferences["session"] = false
    }
}
