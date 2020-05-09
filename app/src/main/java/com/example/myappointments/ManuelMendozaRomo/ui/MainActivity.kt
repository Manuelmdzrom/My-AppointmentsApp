package com.example.myappointments.ManuelMendozaRomo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myappointments.ManuelMendozaRomo.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import com.example.myappointments.ManuelMendozaRomo.PreferenceHelper.get
import com.example.myappointments.ManuelMendozaRomo.PreferenceHelper.set
import com.example.myappointments.ManuelMendozaRomo.R
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val snackBar by lazy {
        Snackbar.make(mainLayout,
            R.string.press_back_again, Snackbar.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Variable para iniciar sesión //Persitencia de datos //Shared preference,sqlLite,files
        /*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val session = preferences.getBoolean("active_session", false)
         */
        val preferences =
            PreferenceHelper.defaultPrefs(
                this
            )
        if(preferences["session", false])
            goToMenuActivity()

        btnLogin.setOnClickListener(){
            //validates
            createSessionPreferences()
            goToMenuActivity()
        }
        tvGoToRegister.setOnClickListener {
            Toast.makeText(this,getString(R.string.please_fill_your_register_data), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createSessionPreferences(){
        /*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", true)
        editor.apply()
        */
        val preferences =
            PreferenceHelper.defaultPrefs(
                this
            )
        preferences["session"] = true
    }

    private fun goToMenuActivity(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed(){
        if (snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()
    }
}
