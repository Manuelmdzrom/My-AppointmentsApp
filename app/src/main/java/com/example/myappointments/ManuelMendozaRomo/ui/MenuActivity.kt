package com.example.myappointments.ManuelMendozaRomo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myappointments.ManuelMendozaRomo.R
import com.example.myappointments.ManuelMendozaRomo.io.ApiService
import com.example.myappointments.ManuelMendozaRomo.util.PreferenceHelper
import com.example.myappointments.ManuelMendozaRomo.util.PreferenceHelper.get
import com.example.myappointments.ManuelMendozaRomo.util.PreferenceHelper.set
import com.example.myappointments.ManuelMendozaRomo.util.toast
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private val apiService by lazy {
        ApiService.create()
    }

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val storeToken =intent.getBooleanExtra("store_token", false)
        if(storeToken)
            storeToken()

        btnCreateAppointment.setOnClickListener {
            val intent = Intent (this, CreatAppointmentActivity::class.java)
            startActivity(intent)
        }
        btnMyAppointments.setOnClickListener {
            val intent = Intent (this, AppointmentsActivity::class.java)
            startActivity(intent)
        }
        btnLogOut.setOnClickListener{
            performLogout()
            clearSessionPreferences()
            val intent = Intent (this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun storeToken(){
        val jwt = preferences["jwt", ""]
        val authHeader = "Bearer $jwt"

        //Firebase Instance
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val deviceToken = instanceIdResult.token
            val call = apiService.postToken(authHeader, deviceToken)
            call.enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    toast(t.localizedMessage)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        Log.d(Companion.TAG, "Token registrado correctamente")
                    } else {
                        Log.d(Companion.TAG, "Hubo un problema al registrar el token")
                    }
                }

            })
        }
    }

    private fun performLogout(){
        val jwt = preferences["jwt", ""]
        val call = apiService.postLogout("Bearer $jwt")
        call.enqueue(object: Callback<Void>{
            override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                clearSessionPreferences()

                val intent = Intent (this@MenuActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun clearSessionPreferences(){
        /*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", false)
        editor.apply()
         */
        preferences["jwt"] = ""
    }

    companion object {
        private const val TAG = "MenuActivity"
    }
}
