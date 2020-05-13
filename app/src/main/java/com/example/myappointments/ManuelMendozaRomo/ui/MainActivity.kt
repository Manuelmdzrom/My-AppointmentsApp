package com.example.myappointments.ManuelMendozaRomo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myappointments.ManuelMendozaRomo.util.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import com.example.myappointments.ManuelMendozaRomo.util.PreferenceHelper.get
import com.example.myappointments.ManuelMendozaRomo.util.PreferenceHelper.set
import com.example.myappointments.ManuelMendozaRomo.R
import com.example.myappointments.ManuelMendozaRomo.io.ApiService
import com.example.myappointments.ManuelMendozaRomo.io.response.LoginResponse
import com.example.myappointments.ManuelMendozaRomo.util.toast
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

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
        if(preferences["jwt", ""].contains("."))
            goToMenuActivity()

        btnLogin.setOnClickListener(){
            //validates
            performLogin()
        }
        tvGoToRegister.setOnClickListener {
            Toast.makeText(this,getString(R.string.please_fill_your_register_data), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.trim().isEmpty() || password.trim().isEmpty())
        {
            toast(getString(R.string.error_empty_credentials))
        }

        val call = apiService.postLogin(email, password)
        call.enqueue(object: Callback<LoginResponse>{
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    val loginResponse = response.body()
                    if (loginResponse == null){
                        toast(getString(R.string.error_login_response))
                        return
                    }
                    if (loginResponse.success){
                        createSessionPreferences(loginResponse.jwt)
                        toast(getString(R.string.welcome_name, loginResponse.user.name))
                        goToMenuActivity()
                    }   else{
                        toast(getString(R.string.error_invalid_credentials))
                    }
                } else {
                    toast(getString(R.string.error_login_response))
                }
            }
        })
    }

    private fun createSessionPreferences(jwt: String){
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
        preferences["jwt"] = jwt
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
