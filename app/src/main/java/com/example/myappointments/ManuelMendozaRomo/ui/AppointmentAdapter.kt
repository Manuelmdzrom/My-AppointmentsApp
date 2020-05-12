package com.example.myappointments.ManuelMendozaRomo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myappointments.ManuelMendozaRomo.Appointment
import com.example.myappointments.ManuelMendozaRomo.R
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter
    : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    var appointments = ArrayList<Appointment>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(appointment: Appointment) {
            with(itemView) {
                tvAppointmentId.text =
                    itemView.context.getString(R.string.item_appointment_id, appointment.id)
                tvDoctorName.text = appointment.doctor.name
                tvScheduleDate.text = itemView.context.getString(
                    R.string.item_appointment_date,
                    appointment.scheduleDate
                )
                tvScheduleTime.text = itemView.context.getString(
                    R.string.item_appointment_time,
                    appointment.scheduleTime
                )

                tvSpecialty.text = appointment.specialty.name
                tvDescription.text = appointment.description
                tvStatus.text = appointment.status
                tvType.text = appointment.type
                tvCreateAt.text =
                    context.getString(R.string.label_appointment_created_at, appointment.createAd)

                ibExpand.setOnClickListener {
                    if (linearLayoutDetails.visibility == View.VISIBLE) {
                        linearLayoutDetails.visibility = View.GONE
                        ibExpand.setImageResource(R.drawable.ic_expand_more)
                    } else {
                        linearLayoutDetails.visibility = View.VISIBLE
                        ibExpand.setImageResource(R.drawable.ic_expand_less)
                    }
                }
            }
        }
    }
    //Inflate XML items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_appointment,
                parent,
                false
            )
        )
    }
    //Bind data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)

    }
    //Number of elements
    override fun getItemCount(): Int {
        return appointments.size
    }
}