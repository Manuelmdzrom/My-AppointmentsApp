package com.example.myappointments.ManuelMendozaRomo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_appointments.*

class AppointmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        val appointments = ArrayList<Appointment>()

        appointments.add(Appointment(1,"Médico AA","12/12/2020","3:00 PM"))
        appointments.add(Appointment(2,"Médico BB","12/01/2020","4:00 PM"))
        appointments.add(Appointment(3,"Médico CC","12/02/2020","5:00 PM"))

        rvAppointments.layoutManager = LinearLayoutManager(this)  //GridLayoutManager
        rvAppointments.adapter = AppointmentAdapter(appointments)
    }
}
