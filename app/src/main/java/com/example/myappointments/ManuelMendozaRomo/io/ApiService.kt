package com.example.myappointments.ManuelMendozaRomo.io

import com.example.myappointments.ManuelMendozaRomo.model.Specialty
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("specialties")
    abstract fun getSpecialties(): Call<ArrayList<Specialty>>

    companion object Factory {
        private const val BASE_URL = "http://161.35.136.222/api/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}