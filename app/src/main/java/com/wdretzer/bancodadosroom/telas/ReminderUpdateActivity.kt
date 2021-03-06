package com.wdretzer.bancodadosroom.telas

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.alarm.AlarmReceiver
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.recycler.ItensAdapter
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel
import java.util.*


class ReminderUpdateActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private val viewModelApp: AppViewModel by viewModels()
    private val textTitleEdit: EditText? by lazy { findViewById(R.id.titulo_input_edit) }
    private val textDescriptionEdit: EditText? by lazy { findViewById(R.id.descricao_input_edit) }
    private val textDateEdit: TextView? by lazy { findViewById(R.id.data_input_edit) }
    private val textTimeEdit: TextView? by lazy { findViewById(R.id.time_input_edit) }
    private val btnSave: Button? by lazy { findViewById(R.id.btn_salve_edit) }
    private val alarmSwitch: SwitchCompat? by lazy { findViewById(R.id.alarm_status_edit) }
    private val statusLembreteSwitch: SwitchCompat? by lazy { findViewById(R.id.lembrete_status_edit) }

    private val loading: FrameLayout
        get() = findViewById(R.id.loading)

    var day = 0
    var month = 0
    var year = 0
    var seconds = 0
    var minutes = 0
    var hour = 0
    var amPm = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    var savedMinutes = 0
    var savedHour = 0

    var savedMinutesText = ""
    var savedHourText = ""
    var idBundle: Int = 0
    var getRequestCode: Int = 0
    var checkTimeUpdate: String = ""
    var checkDateUpdate: String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_update)

        getSupportActionBar()?.hide()

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val getTextTitle = bundle.getString("Titulo")
            val getTextDescription = bundle.getString("Desc")
            val getTextDate = bundle.getString("Data")
            val getTextTime = bundle.getString("Hora")
            val getStatusAlarm = bundle.getBoolean("Alarme")
            val getStatusLembrete = bundle.getBoolean("Status")

            idBundle = bundle.getInt("Id")
            getRequestCode = bundle.getInt("Code")

            getTextTitle?.let { textTitleEdit?.setText(it) }
            getTextDescription?.let { textDescriptionEdit?.setText(it) }
            getTextDate?.let {
                textDateEdit?.text = it
                checkDateUpdate = it
            }
            getTextTime?.let {
                textTimeEdit?.text = it
                checkTimeUpdate = it
            }

            alarmSwitch!!.isChecked = getStatusAlarm
            statusLembreteSwitch!!.isChecked = getStatusLembrete
            idBundle
            getRequestCode
        }

        textDateEdit?.setOnClickListener { pickDate() }
        textTimeEdit?.setOnClickListener { pickTime() }
        btnSave?.setOnClickListener { verifyDataTimeReminderOnDB() }
    }


    private fun getDateCalendar() {
        val myCalendar = Calendar.getInstance()
        day = myCalendar.get(Calendar.DAY_OF_MONTH)
        month = myCalendar.get(Calendar.MONTH)
        year = myCalendar.get(Calendar.YEAR)
        amPm = myCalendar.get(Calendar.AM_PM)

        seconds = myCalendar.get(Calendar.SECOND)
        minutes = myCalendar.get(Calendar.MINUTE)
        hour = myCalendar.get(Calendar.HOUR)
    }


    private fun updateItem(item: InfoDados) {
        viewModelApp.updateItem(item).observe(this) {
            if (it is DataResult.Loading) {
                loading.isVisible = it.isLoading
            }

            if (it is DataResult.Error) {
                Toast.makeText(
                    this,
                    "Erro em atualizar os dados do Lembrete!",
                    Toast.LENGTH_LONG
                ).show()

                Log.d(
                    "Update Lembrete:",
                    "Erro ao realizar update do item do Lembrete! Erro: $it"
                )
            }

            if (it is DataResult.Empty) {
                Log.d(
                    "Update Lembrete:",
                    "Retorno vazio: $it ao realizar update do item do Lembrete!"
                )
                Toast.makeText(this, "Retorno Vazio!", Toast.LENGTH_LONG).show()
            }

            if (it is DataResult.Success) {
                Toast.makeText(this, "Os dados foram atualizados com sucesso!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun verifyDataTimeReminderOnDB() {
        val timeInput = textTimeEdit?.text.toString()
        val data = textDateEdit?.text.toString()
        var check = false

        if (checkTimeUpdate == timeInput && checkDateUpdate == data) {
            updateItem(
                InfoDados(
                    textTitleEdit?.text.toString(),
                    textDescriptionEdit?.text.toString(),
                    textDateEdit?.text.toString(),
                    textTimeEdit?.text.toString(),
                    alarmSwitch!!.isChecked,
                    idBundle,
                    getRequestCode,
                    statusLembreteSwitch!!.isChecked
                )
            )
            setAlarm(getRequestCode)
            sendToListaBD()

        } else {

            viewModelApp.listItensToday(data).observe(this) {
                if (it is DataResult.Loading) {
                    loading.isVisible = it.isLoading
                }

                if (it is DataResult.Error) {
                    Toast.makeText(
                        this,
                        "Erro ao realizar update do Lembrete!",
                        Toast.LENGTH_LONG
                    ).show()

                    Log.d(
                        "Update Lembrete:",
                        "Erro ao ao realizar update dos Lembretes! Erro: $it"
                    )
                }

                if (it is DataResult.Empty) {
                    Log.d(
                        "Update Lembrete:",
                        "Retorno vazio: $it ao realizar update dos dados do Lembretes!"
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
                            "J?? existe uma tarefa salva com o mesmo horario!!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        updateItem(
                            InfoDados(
                                textTitleEdit?.text.toString(),
                                textDescriptionEdit?.text.toString(),
                                textDateEdit?.text.toString(),
                                textTimeEdit?.text.toString(),
                                alarmSwitch!!.isChecked,
                                idBundle,
                                getRequestCode,
                                statusLembreteSwitch!!.isChecked
                            )
                        )
                        setAlarm(getRequestCode)
                        sendToListaBD()
                    }
                }
            }
        }
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


    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month + 1
        savedYear = year
        textDateEdit?.setText("$savedDay/$savedMonth/$savedYear")
    }


    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinutes = minute

        savedMinutesText = if (minute < 10) "0$minute" else "$minute"
        savedHourText = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
        textTimeEdit?.text = "$savedHourText:$savedMinutesText" + "hr"
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm(getRequestCode: Int) {

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
            putExtra("Titulo", textTitleEdit?.text.toString())
            putExtra("Description", textDescriptionEdit?.text.toString())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            getRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (alarmSwitch!!.isChecked) {
            alarmM.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Toast.makeText(
                this,
                "Alarme do lembrete foi ativado com sucesso!",
                Toast.LENGTH_SHORT
            )
                .show()

        } else {
            alarmM.cancel(pendingIntent)
            Toast.makeText(this, "Alarme do lembrete n??o ativado!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun sendToListaBD() {
        Handler().postDelayed({
            val intent = Intent(this, ListRemindersSaveActivity::class.java)
            startActivity(intent)
        }, 2000)
    }

}
