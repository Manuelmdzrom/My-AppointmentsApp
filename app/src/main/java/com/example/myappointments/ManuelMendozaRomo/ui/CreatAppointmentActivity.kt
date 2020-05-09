package com.example.myappointments.ManuelMendozaRomo.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import com.example.myappointments.ManuelMendozaRomo.R
import com.example.myappointments.ManuelMendozaRomo.io.ApiService
import com.example.myappointments.ManuelMendozaRomo.model.Specialty
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_appointment.*
import kotlinx.android.synthetic.main.card_view_step_one.*
import kotlinx.android.synthetic.main.card_view_step_one.cvStep1
import kotlinx.android.synthetic.main.card_view_step_three.*
import kotlinx.android.synthetic.main.card_view_step_two.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CreatAppointmentActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    private var selectedcalendar = Calendar.getInstance()
    private var selectedTimeRadioBtn: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {
            if (etDescription.text.toString().length <3){
                etDescription.error = getString(R.string.validate_appointment_description)
            } else {
                //Continue Step Two
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
        }

        btnNext2.setOnClickListener{
            if (etScheduleDate.text.toString().isEmpty()) //validar la fecha
            {
                etScheduleDate.error = getString(R.string.validate_appointment_date)
            } else if (selectedTimeRadioBtn?.text.toString().isEmpty()) //validar que un radio button este seleccionado
            {
                Snackbar.make(createAppointmentLinearLayout,
                    R.string.validate_appointment_time, Snackbar.LENGTH_SHORT).show()
            } else {
                //Continue Step Two
                showAppointmentDataToConfirm()
                cvStep2.visibility = View.GONE
                cvStep3.visibility = View.VISIBLE
                    }
            }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "Cita registrada correctamente", Toast.LENGTH_SHORT).show()
            finish() // Close activities
        }

        loadSpecialties()

        val doctorsOptions = arrayOf("Doctor A","Docotr B","Doctor C")
        spinnerDoctors.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, doctorsOptions)
    }

    private fun loadSpecialties(){
        val call = apiService.getSpecialties()
        call.enqueue(object : Callback<ArrayList<Specialty>>{
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(this@CreatAppointmentActivity, getString(R.string.error_login_specialties), Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onResponse(call: Call<ArrayList<Specialty>>,response: Response<ArrayList<Specialty>>) {
                if (response.isSuccessful) { // 200...300
                    val specialties = response.body()

                    val specialtyOptions = ArrayList<String>()

                    specialties?.forEach {
                        specialtyOptions.add(it.name)
                    }
                    spinnerSpecialties.adapter = ArrayAdapter<String>(this@CreatAppointmentActivity, android.R.layout.simple_list_item_1, specialtyOptions)
                }
            }
        })
        //Se cargaba el spinner de manera constante
        //val specialtiesOptions = arrayOf("Specialty A","Specialty B","Specialty C")
        //spinnerSpecialties.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, specialtiesOptions)
    }

    private fun showAppointmentDataToConfirm(){
        tvConfirmDescription.text = etDescription.text.toString()
        tvConfirmSpecialty.text = spinnerSpecialties.selectedItem.toString()
        val selectedradioBtnId = radioGroupType.checkedRadioButtonId  // Acceder al Id
        val selectedRadioType = radioGroupType.findViewById<RadioButton>(selectedradioBtnId) // buscamos el radio seleccionado
        tvConfirmType.text = selectedRadioType.text.toString()

        tvConfirmDoctor.text = spinnerDoctors.selectedItem.toString()
        tvConfirmScheduleDate.text = etScheduleDate.text.toString()
        tvConfirmScheduleTime.text = selectedTimeRadioBtn?.text.toString()
    }

    fun onclickScheduleDate(v: View?){
        // Llenar el Date creando Instancia en calendario

        val year = selectedcalendar.get(Calendar.YEAR)
        val month = selectedcalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedcalendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener{datePicker,y,m,d->
            //Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_SHORT).show()
            //Pasar la decha al EditText
            selectedcalendar.set(y, m, d)
            etScheduleDate.setText(
                resources.getString(
                    R.string.date_format,
                    y,
                    m.twoDigits(),
                    d.twoDigits()
                )
            )
            etScheduleDate.error = null
            displayRadioButtons()
        }
        //min date
        //Max date
        //new dialog
        val datePickerDialog = DatePickerDialog(this, listener, year, month, dayOfMonth)
        //set Limits
        val datePicker = datePickerDialog.datePicker
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        datePicker.minDate =  selectedcalendar.timeInMillis //+1 now
        calendar.add(Calendar.DAY_OF_MONTH,29)
        datePicker.maxDate = calendar.timeInMillis  // +30

        //show Dialog
        datePickerDialog.show()

    }

    private fun displayRadioButtons(){
        //radioGroup.clearCheck()
        //radioGroup.removeAllViews()
        //radioGroupLeft.checkedRadioButtonId
        selectedTimeRadioBtn = null  // Unselected
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        val hours = arrayOf("3:00 PM","3:30 PM","4:00 PM","4:30 PM")
        var goToLeft = true
        //foreach para cada hora y agregar a cada radio button
        hours.forEach{
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            //Funcionalidadpara marcar y desmarcar RadioButton
            radioButton.setOnClickListener{view->
                selectedTimeRadioBtn?.isChecked = false

                selectedTimeRadioBtn = view as RadioButton?
                selectedTimeRadioBtn?.isChecked = true
            }

            if (goToLeft)
                radioGroupLeft.addView(radioButton)
            else
                radioGroupRight.addView(radioButton)
            goToLeft = !goToLeft
            }
    }

    fun Int.twoDigits(): String = if(this>=10) this.toString() else "0$this"

    override fun onBackPressed() {
        when {
            cvStep3.visibility == View.VISIBLE -> {
                cvStep3.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
            cvStep2.visibility== View.VISIBLE -> {
                cvStep2.visibility = View.GONE
                cvStep1.visibility = View.VISIBLE

            }
            cvStep1.visibility == View.VISIBLE -> {

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.dialog_create_appointmet_exit_title))
                builder.setMessage(getString(R.string.dialog_creat_appointment_exit_message))
                builder.setPositiveButton(getString(R.string.dialog_create_appointement_exit_positive_btn)) { _, _ ->
                    finish()
                }
                builder.setNegativeButton(getString(R.string.dialog_create_appointement_exit_negative_btn)) { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        }
    }
}

