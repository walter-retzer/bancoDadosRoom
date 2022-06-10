package com.wdretzer.bancodadosroom

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.wdretzer.bancodadosroom.alarm.AlarmReceiver
import com.wdretzer.bancodadosroom.bd.ListaBD
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel
import java.util.*


class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private val viewModelApp: AppViewModel by viewModels()

    private val btnSalvarLembrete: Button
        get() = findViewById(R.id.btn_continue)

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
    var savedMinutes = 0
    var savedHour = 0

    var savedMinutesText = ""
    var savedHourText = ""


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportActionBar()?.hide()

        textData?.setOnClickListener { pickDate() }
        textHorario?.setOnClickListener { pickTime() }
        btnSalvarLembrete.setOnClickListener {
            setAlarm()
            checkInfo()
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkInfo() {
        if (textTitulo?.text.toString().isEmpty() ||
            textDescricao?.text.toString().isEmpty() ||
            textData?.text.toString() == "dd/mm/aaaa" ||
            textHorario?.text.toString() == "--:-- hr"
        ) {

            Toast.makeText(this, "Há campos não preenchidos!", Toast.LENGTH_SHORT).show()

        } else {
            saveToDoList()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun saveToDoList() {

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, savedDay)
        calendar.set(Calendar.MONTH, savedMonth)
        calendar.set(Calendar.YEAR, savedYear)
        calendar.set(Calendar.HOUR_OF_DAY, savedHour)
        calendar.set(Calendar.MINUTE, savedMinutes)
        calendar.set(Calendar.SECOND, 0)

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

        savedHour = hourOfDay
        savedMinutes = minute

        savedMinutesText = if (minute < 10) "0$minute" else "$minute"
        savedHourText = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
        textHorario?.text = "$savedHourText:$savedMinutesText" + "hr"

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm() {

        val calendar: Calendar = Calendar.getInstance().apply {

            set(Calendar.HOUR_OF_DAY, savedHour)
            set(Calendar.MINUTE, savedMinutes)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val alarmM = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        val pendingIntent = getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        alarmM.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

//        val intent2 = Intent(this, AlarmReceiver::class.java)
//        intent.action = "SomeAction"
//        val pendingIntent2 =
//            PendingIntent.getBroadcast(
//                this,
//                ALARM_REQUEST_CODE,
//                intent2,
//                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
//            )
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager?
//        if (pendingIntent2 != null) {
//            alarmManager!!.cancel(pendingIntent)
//        }

        Toast.makeText(this, "Alarm is done set: ${calendar.time}", Toast.LENGTH_SHORT).show()

    }

    companion object {
        private const val ALARM_REQUEST_CODE = 1000
    }
}
