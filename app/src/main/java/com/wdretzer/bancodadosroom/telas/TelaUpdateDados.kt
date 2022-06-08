package com.wdretzer.bancodadosroom.telas

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
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.bd.ListaBD
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel
import java.util.*

class TelaUpdateDados : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private val viewModelApp: AppViewModel by viewModels()

    private val btnSave: Button
        get() = findViewById(R.id.btn_continue)

    private val textTitleEdit: TextInputEditText
        get() = findViewById(R.id.titulo_input_text)

    private val textDescriptionEdit: TextInputEditText
        get() = findViewById(R.id.descricao_input_text)

    private val textDateEdit: TextInputEditText
        get() = findViewById(R.id.data_input_text)

    private val textTimeEdit: TextInputEditText
        get() = findViewById(R.id.horario_input_text)

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
        setContentView(R.layout.activity_tela_update_dados)

        getSupportActionBar()?.hide()

        val bundle: Bundle? = intent.extras
        var idBundle: Int = 0

        if (bundle != null) {
            val getTextTitle = bundle.getString("Titulo")
            val getTextDescription = bundle.getString("Desc")
            val getTextDate = bundle.getString("Data")
            val getTextTime = bundle.getString("Hora")
            val getId = bundle.getInt("Id")

            getTextTitle?.let { textTitleEdit.setText(it) }
            getTextDescription?.let { textDescriptionEdit.setText(it) }
            getTextDate?.let { textDateEdit.setText(it) }
            getTextTime?.let { textTimeEdit.setText(it) }
            idBundle = getId
        }

        textDateEdit.setOnClickListener {
            pickDate()
        }

        textTimeEdit.setOnClickListener {
            pickTime()
        }

        btnSave.setOnClickListener {
            updateItem(
                InfoDados(
                    textTitleEdit.text.toString(),
                    textDescriptionEdit.text.toString(),
                    textDateEdit.text.toString(),
                    textTimeEdit.text.toString(),
                    idBundle
                )
            )
            sendToListaBD()
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

    private fun updateItem(item: InfoDados) {
        viewModelApp.updateItem(item).observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Update Sucess!", Toast.LENGTH_SHORT).show()
            }

            if (it is DataResult.Error) {
                Toast.makeText(this, "Update Error!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickDate() {
        getDateCalendar()
        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun pickTime() {
        getDateCalendar()
        TimePickerDialog(this, this, hour, minutes, true).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month + 1
        savedYear = year
        textDateEdit.setText("$savedDay/$savedMonth/$savedYear")
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedMinutes = if (minute < 10) "0$minute" else "$minute"
        savedHour = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"

        textTimeEdit.setText("$savedHour:$savedMinutes" + "hr")
    }

    private fun sendToListaBD() {
        Handler().postDelayed({
            val intent = Intent(this, ListaBD::class.java)
            startActivity(intent)
        }, 2000)
    }

}
