package com.wdretzer.bancodadosroom.telas

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.alarm.AlarmReceiver
import com.wdretzer.bancodadosroom.alarm.AlarmRing
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.recycler.ItensAdapter
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel
import java.util.*


class ListTodayActivity : AppCompatActivity() {

    private val viewModelApp: AppViewModel by viewModels()

    private val btnHomeMenu: ShapeableImageView
        get() = findViewById(R.id.btn_send_home)

    private val btnAddItem: ShapeableImageView
        get() = findViewById(R.id.btn_add_list_today)

    private val btnSearch: ShapeableImageView
        get() = findViewById(R.id.btn_search)

    private val textTotalItens: TextView
        get() = findViewById(R.id.total_itens_today)

    private val textDataAtual: TextView
        get() = findViewById(R.id.tarefas2_today)

    var totalItens: Int = 0
    var day = 0
    var month = 0
    var year = 0


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_today)

        getSupportActionBar()?.hide()
        stopRingtoneAlarm()

        AlarmRing.r?.stop()
        getDateCalendar()
        countItem("$day/$month/$year")
        showListTodayBD("$day/$month/$year")

        btnHomeMenu.setOnClickListener { sendToListBD() }
        btnAddItem.setOnClickListener { sendToMainActivity() }
        btnSearch.setOnClickListener { sendToSearchList() }
    }


    override fun onBackPressed() {
        val intent = Intent(this, ListRemindersSaveActivity::class.java)
        startActivity(intent)
    }


    private fun stopRingtoneAlarm() {
        val i = Intent(this, AlarmRing::class.java)
        stopService(i)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun showListTodayBD(data: String) {
        viewModelApp.listItensToday(data).observe(this) {

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_today)
            val adapter = ItensAdapter(::updateItem, { itens ->
                showDialogDeleteItem("Deseja realmente apagar esse Lembrete?", itens)
            }) {}
            adapter.updateList(it)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }


    private fun updateItem(item: InfoDados) {
        sendToTelaUpdate(item)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun deleteItem(item: InfoDados) {
        viewModelApp.deleteItem(item).observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Lembrete deletado com sucesso!", Toast.LENGTH_SHORT).show()
                showListTodayBD("$day/$month/$year")
                countItem("$day/$month/$year")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun countItem(item: String) {
        viewModelApp.countItens(item).observe(this) {
            textTotalItens.text = it.toString()
            showListTodayBD("$day/$month/$year")
        }
    }


    @SuppressLint("SetTextI18n")
    private fun getDateCalendar() {
        val myCalendar = Calendar.getInstance()
        day = myCalendar.get(Calendar.DAY_OF_MONTH)
        month = myCalendar.get(Calendar.MONTH) + 1
        year = myCalendar.get(Calendar.YEAR)

        textDataAtual.text = "$day/$month/$year"
    }


    private fun sendToTelaUpdate(info: InfoDados) {
        val titulo = info.tituloInfo
        val descricao = info.descricaoInfo
        val data = info.dataInfo
        val horario = info.horarioInfo
        val id = info.idUser

        val intent = Intent(this, ReminderUpdateActivity::class.java).apply {
            putExtra("Titulo", titulo)
            putExtra("Desc", descricao)
            putExtra("Data", data)
            putExtra("Hora", horario)
            putExtra("Id", id)
        }
        startActivity(intent)
    }


    private fun sendToListBD() {
        val intent = Intent(this, ListRemindersSaveActivity::class.java)
        startActivity(intent)
    }


    private fun sendToMainActivity() {
        val intent = Intent(this, InsertReminderActivity::class.java)
        startActivity(intent)
    }


    private fun sendToSearchList() {
        val intent = Intent(this, SearchListActivity::class.java)
        startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun showDialogDeleteItem(title: String, itens: InfoDados) {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.fragment_dialog_delete)

        val body = dialog.findViewById(R.id.frag_title) as TextView
        body.text = title
        val btnApagar = dialog.findViewById(R.id.btn_apagar) as Button
        val btnCancelar = dialog.findViewById(R.id.btn_cancelar) as TextView

        btnApagar.setOnClickListener {
            deleteItem(itens)
            resetAlarm(itens)
            dialog.dismiss()
        }
        btnCancelar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun resetAlarm(itens: InfoDados) {

        if (itens.alarmStatusInfo) {
            val alarmM = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, AlarmReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                itens.requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmM.cancel(pendingIntent)
            //Toast.makeText(this, "Alarm Resetado!", Toast.LENGTH_SHORT).show()
        }
    }
}
