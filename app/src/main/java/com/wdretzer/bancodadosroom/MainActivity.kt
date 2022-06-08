package com.wdretzer.bancodadosroom

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.wdretzer.bancodadosroom.bd.ListaBD
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel
import java.util.*


class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private val viewModelApp: AppViewModel by viewModels()

    private val btnCadastrar: Button
        get() = findViewById(R.id.btn_continue)

//    private val textTitulo: TextInputEditText
//        get() = findViewById(R.id.titulo_input_text)
//
//    private val textDescricao: TextInputEditText
//        get() = findViewById(R.id.descricao_input_text)

    private val loading: FrameLayout
        get() = findViewById(R.id.loading)

    private val textTitulo: EditText? by lazy { findViewById(R.id.titulo_input) }

    private val textDescricao: EditText? by lazy { findViewById(R.id.descricao_input) }

    private val textData: TextView? by lazy { findViewById(R.id.data_input) }

    private val textHorario: TextView? by lazy { findViewById(R.id.time_input) }

    var day = 0
    var month = 0
    var year = 0
    var seconds = 0
    var minutes = 0
    var hour = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedMinutes = ""
    var savedHour = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportActionBar()?.hide()

        textData?.setOnClickListener {
            pickDate()
        }

        textHorario?.setOnClickListener {
            pickTime()
        }

        btnCadastrar.setOnClickListener {
            checkInfo()
        }
    }

    private fun checkInfo() {
        if (textTitulo?.text.toString().isEmpty() ||
            textDescricao?.text.toString().isEmpty() ||
            textData?.text.toString() == "dd/mm/aaaa" ||
            textHorario?.text.toString() == "--:-- hr") {

            Toast.makeText(this, "Há campos não preenchidos!", Toast.LENGTH_SHORT).show()

        } else {
            saveToDoList()
            sendToListaBD()
        }
    }

    private fun saveToDoList() {

        viewModelApp.addOrRemoveItens(

            textTitulo?.text.toString(),
            textDescricao?.text.toString(),
            textData?.text.toString(),
            textHorario?.text.toString()

        ).observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Dados Salvos no Banco de Dados!", Toast.LENGTH_SHORT).show()
            }

            if (it is DataResult.Error) {
                Toast.makeText(this, "Error ao Salvar os Dados!", Toast.LENGTH_SHORT).show()
            }

            if (it is DataResult.Loading) {
                loading.isVisible = it.isLoading
            }
        }
    }

    private fun getDateCalendar() {
        val myCalendar = Calendar.getInstance()
        day = myCalendar.get(Calendar.DAY_OF_MONTH)
        month = myCalendar.get(Calendar.MONTH)
        year = myCalendar.get(Calendar.YEAR)

        seconds = myCalendar.get(Calendar.SECOND)
        minutes = myCalendar.get(Calendar.MINUTE)
        hour = myCalendar.get(Calendar.HOUR)
    }

    private fun pickDate() {
        getDateCalendar()
        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun pickTime() {
        getDateCalendar()
        TimePickerDialog(this, this, hour, minutes, false).show()
    }

    private fun sendToListaBD() {
        loading.isVisible = true
        Handler().postDelayed({
            loading.isVisible = false
            val intent = Intent(this, ListaBD::class.java)
            startActivity(intent)
        }, 3000)
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month + 1
        savedYear = year
        textData?.text = "$savedDay/$savedMonth/$savedYear"
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        savedMinutes = if (minute < 10) "0$minute" else "$minute"
        savedHour = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"

        textHorario?.text = "$savedHour:$savedMinutes" + "hr"
    }
}
