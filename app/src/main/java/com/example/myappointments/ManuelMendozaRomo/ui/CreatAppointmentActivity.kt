package com.example.myappointments.ManuelMendozaRomo.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import com.example.myappointments.ManuelMendozaRomo.R
import com.example.myappointments.ManuelMendozaRomo.io.ApiService
import com.example.myappointments.ManuelMendozaRomo.io.response.SimpleResponse
import com.example.myappointments.ManuelMendozaRomo.model.Doctor
import com.example.myappointments.ManuelMendozaRomo.model.Schedule
import com.example.myappointments.ManuelMendozaRomo.model.Specialty
import com.example.myappointments.ManuelMendozaRomo.util.PreferenceHelper
import com.example.myappointments.ManuelMendozaRomo.util.PreferenceHelper.get
import com.example.myappointments.ManuelMendozaRomo.util.toast
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

class CreatAppointmentActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
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
            performStoreAppointment()
        }

        loadSpecialties()
        listenSpecialtiesChanges()
        //val doctorsOptions = arrayOf("Doctor A","Docotr B","Doctor C")
        //spinnerDoctors.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, doctorsOptions)
        listenDoctorAndDateChanges()
    }

    private fun performStoreAppointment(){
        btnConfirmAppointment.isClickable = false

        val jwt = preferences["jwt", ""]
        val authHeader = "Bearer $jwt"
        val description = tvConfirmDescription.text.toString()
        val specialty = spinnerSpecialties.selectedItem as Specialty
        val doctor = spinnerDoctors.selectedItem as Doctor
        val scheduleDate = tvConfirmScheduleDate.text.toString()
        val scheduleTime = tvConfirmScheduleTime.text.toString()
        val type = tvConfirmType.text.toString()

        val call = apiService.storeAppointment(
            authHeader,
            description,
            specialty.id,
            doctor.id,
            scheduleDate,
            scheduleTime,
            type)
        call.enqueue(object: Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                if (response.isSuccessful) {
                    toast(getString(R.string.create_appointment_success))
                    finish() // Close activities
                } else {
                   toast(getString(R.string.create_appointment_error))
                    btnConfirmAppointment.isClickable = true
                }
            }
        })
    }

    private fun listenDoctorAndDateChanges(){
        // doctors
        spinnerDoctors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val doctor = adapter?.getItemAtPosition(position) as Doctor
                loadHours(doctor.id, etScheduleDate.text.toString())
                Toast.makeText(this@CreatAppointmentActivity, doctor.name, Toast.LENGTH_SHORT).show()
            }
        }
        //Schedule Data
        etScheduleDate.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val doctor= spinnerDoctors.selectedItem as Doctor
                loadHours(doctor.id, etScheduleDate.text.toString())
            }
        })
    }
    
    private fun loadHours(doctorId:Int, date: String){
        if (date.isEmpty()){
            return
        }

        val call = apiService.getHours(doctorId, date)
        call.enqueue(object: Callback<Schedule> {
            override fun onFailure(call: Call<Schedule>, t: Throwable) {
                Toast.makeText(this@CreatAppointmentActivity,getString(R.string.error_loading_hours),Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                if (response.isSuccessful){
                    val schedule = response.body()
                    //Toast.makeText(this@CreatAppointmentActivity, "morning: ${schedule?.morning?.size} , afternoon: ${schedule?.afternoon?.size}", Toast.LENGTH_SHORT).show()
                    schedule?.let {
                        tvSelectDoctorAndDate.visibility = View.GONE

                        val intervals = it.morning + it.afternoon
                        val hours = ArrayList<String>()
                        intervals.forEach{ interval ->
                            hours.add(interval.start)
                        }
                        displayIntervalRadios(hours)
                    }
                }
            }
        })
        //Toast.makeText(this, "doctor: $doctorId, date: $date",Toast.LENGTH_SHORT).show()
    }

    private fun loadSpecialties(){
        val call = apiService.getSpecialties()
        call.enqueue(object : Callback<ArrayList<Specialty>>{
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(this@CreatAppointmentActivity, getString(R.string.error_loading_specialties), Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onResponse(call: Call<ArrayList<Specialty>>,response: Response<ArrayList<Specialty>>) {
                response.body()?.let {
                    val specialties = it.toMutableList()
                    spinnerSpecialties.adapter = ArrayAdapter<Specialty>(this@CreatAppointmentActivity, android.R.layout.simple_list_item_1, specialties)
                }
            }
        })
        //Se cargaba el spinner de manera constante
        //val specialtiesOptions = arrayOf("Specialty A","Specialty B","Specialty C")
        //spinnerSpecialties.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, specialtiesOptions)
    }

    private fun listenSpecialtiesChanges() {
        spinnerSpecialties.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
               val specialty = adapter?.getItemAtPosition(position) as Specialty
                loadDoctors(specialty.id)
                Toast.makeText(this@CreatAppointmentActivity, specialty.name, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadDoctors(specialtyid: Int){
      val call = apiService.getDoctors(specialtyid)
        call.enqueue(object: Callback<ArrayList<Doctor>>{
            override fun onFailure(call: Call<ArrayList<Doctor>>, t: Throwable) {
                Toast.makeText(this@CreatAppointmentActivity, getString(R.string.error_loading_doctors), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ArrayList<Doctor>>, response: Response<ArrayList<Doctor>>
            ) {
                  response.body()?.let {
                    val doctors = it.toMutableList()
                    spinnerDoctors.adapter = ArrayAdapter<Doctor>(this@CreatAppointmentActivity, android.R.layout.simple_list_item_1, doctors)
                }
            }

        })
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
                    (m+1).twoDigits(),
                    d.twoDigits()
                )
            )
            etScheduleDate.error = null
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

    private fun displayIntervalRadios(hours: ArrayList<String>){
        //radioGroup.clearCheck()
        //radioGroup.removeAllViews()
        //radioGroupLeft.checkedRadioButtonId
        selectedTimeRadioBtn = null  // Unselected
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        if(hours.isEmpty()){
            tvNotAvailableHours.visibility = View.VISIBLE
            return
        }

        tvNotAvailableHours.visibility = View.GONE
        //val hours = arrayOf("3:00 PM","3:30 PM","4:00 PM","4:30 PM")
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

