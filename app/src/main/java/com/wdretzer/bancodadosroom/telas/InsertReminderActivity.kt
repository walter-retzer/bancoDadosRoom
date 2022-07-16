package com.wdretzer.bancodadosroom.telas

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.getBroadcast
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.alarm.AlarmReceiver
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel
import java.util.*


class InsertReminderActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
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
    private val alarmSwitch: SwitchCompat? by lazy { findViewById(R.id.alarm_status) }

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
    var requestCodeAlarm: Int = 0

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_reminder)

        getSupportActionBar()?.hide()

        textData?.setOnClickListener { pickDate() }
        textHorario?.setOnClickListener { pickTime() }
        btnSalvarLembrete.setOnClickListener { checkInfo() }
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
            verifyDataTimeReminderOnBD()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun saveToDoList(requestCodeAlarm: Int) {

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, savedDay)
        calendar.set(Calendar.MONTH, savedMonth)
        calendar.set(Calendar.YEAR, savedYear)
        calendar.set(Calendar.HOUR_OF_DAY, savedHour)
        calendar.set(Calendar.MINUTE, savedMinutes)
        calendar.set(Calendar.SECOND, 0)

        viewModelApp.addItens(

            textTitulo?.text.toString(),
            textDescricao?.text.toString(),
            textData?.text.toString(),
            textHorario?.text.toString(),
            alarmSwitch!!.isChecked,
            requestCodeAlarm,
            false

        ).observe(this) {
            if (it is DataResult.Success) {
                Toast.makeText(this, "Lembrete salvo com sucesso!", Toast.LENGTH_SHORT).show()
            }

            if (it is DataResult.Empty) {
                Log.d(
                    "Add Lembrete:",
                    "Retorno vazio: $it ao realizar adicionar itens ao Lembrete!"
                )
                Toast.makeText(this, "Retorno Vazio!", Toast.LENGTH_LONG).show()
            }

            if (it is DataResult.Error) {
                Log.d(
                    "Add Lembrete:",
                    "Erro: $it ao realizar adicionar itens ao Lembrete!"
                )
                Toast.makeText(this, "Error ao salvar o lembrete!", Toast.LENGTH_SHORT).show()
            }

            if (it is DataResult.Loading) {
                loading.isVisible = it.isLoading
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun verifyDataTimeReminderOnBD() {
        val timeInput = "$savedHourText:$savedMinutesText" + "hr"
        val data = "$savedDay/$savedMonth/$savedYear"
        var check: Boolean = false

        viewModelApp.listItensToday(data).observe(this) {
            if (it is DataResult.Loading) {
                loading.isVisible = it.isLoading
            }

            if (it is DataResult.Error) {
                Toast.makeText(
                    this,
                    "Erro ao adicionar Lembrete!",
                    Toast.LENGTH_LONG
                ).show()

                Log.d(
                    "Insert Lembrete:",
                    "Erro ao ao adicionar Lembrete! Erro: $it"
                )
            }

            if (it is DataResult.Empty) {
                Log.d(
                    "Insert Lembrete:",
                    "Retorno vazio: $it ao adicioanr Lembrete!"
                )
                Toast.makeText(this, "Retorno Vazio!", Toast.LENGTH_LONG).show()
            }

            if (it is DataResult.Success) {
                it.dataResult.map { dados ->
                    if (timeInput == dados.horarioInfo) {
                        check = true
                    }
                }

                if (check) {
                    Toast.makeText(
                        this,
                        "Já existe uma tarefa salva com o mesmo horario!!",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    sendToListaBD()
                    saveToDoList(requestCodeAlarm)
                    setAlarm(requestCodeAlarm)
                    requestCodeAlarm = System.currentTimeMillis().toInt()
                }
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
        val myCalendar = Calendar.getInstance()
        day = myCalendar.get(Calendar.DAY_OF_MONTH)
        month = myCalendar.get(Calendar.MONTH)
        year = myCalendar.get(Calendar.YEAR)

        val dialog = DatePickerDialog(this, this, year, month, day)
        dialog.datePicker.minDate = myCalendar.timeInMillis
        dialog.show()
    }


    private fun pickTime() {
        getDateCalendar()
        TimePickerDialog(this, this, hour, minutes, true).show()
    }


    private fun sendToListaBD() {
        loading.isVisible = true
        Handler().postDelayed({
            loading.isVisible = false
            val intent = Intent(this, ListRemindersSaveActivity::class.java)
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
    private fun setAlarm(requestCodeAlarm: Int) {

        val calendar: Calendar = Calendar.getInstance().apply {

            set(Calendar.HOUR_OF_DAY, savedHour)
            set(Calendar.MINUTE, savedMinutes)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val alarmM = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("Titulo", textTitulo?.text.toString())
            putExtra("Description", textDescricao?.text.toString())
            putExtra("Id", requestCodeAlarm.toString())
        }

        val pendingIntent = getBroadcast(
            this,
            requestCodeAlarm,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        if (alarmSwitch!!.isChecked) {
            alarmM.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Toast.makeText(this, "Alarme do lembrete foi ativado com sucesso!", Toast.LENGTH_SHORT)
                .show()

        } else {
            alarmM.cancel(pendingIntent)
            Toast.makeText(this, "Alarme do lembrete não ativado!", Toast.LENGTH_SHORT).show()
        }
    }
}
