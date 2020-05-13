package com.example.myappointments.ManuelMendozaRomo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myappointments.ManuelMendozaRomo.R
import com.example.myappointments.ManuelMendozaRomo.io.ApiService
import com.example.myappointments.ManuelMendozaRomo.io.response.LoginResponse
import com.example.myappointments.ManuelMendozaRomo.util.PreferenceHelper
import com.example.myappointments.ManuelMendozaRomo.util.PreferenceHelper.set
import com.example.myappointments.ManuelMendozaRomo.util.toast
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    val apiService by lazy {
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tvGoToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            //finish()  // Con este metodo la activity se cierra
        }
        btnConfirmRegister.setOnClickListener {
            performRegister()
        }
    }

    private fun performRegister(){
        val name = etRegisterName.text.toString().trim()
        val email = etRegisterEmail.text.toString().trim()
        val password = etRegisterPassword.text.toString()
        val password_confirmation = etRegisterPasswordConfirmation.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || password_confirmation.isEmpty()){
            toast(getString(R.string.error_register_empty_files))
            return
        }

        if (password != password_confirmation){
            toast(getString(R.string.error_register_passwords_do_not_match))
            return
        }

        val call = apiService.postRegister(name,email,password,password_confirmation)
        call.enqueue(object: Callback<LoginResponse>{
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
               toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
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
                        toast(getString(R.string.error_register_validataion))
                    }
                } else {
                    toast(getString(R.string.error_register_validataion))
                }
            }
        })
    }

    private fun createSessionPreferences(jwt: String){
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["jwt"] = jwt
    }

    private fun goToMenuActivity(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}
