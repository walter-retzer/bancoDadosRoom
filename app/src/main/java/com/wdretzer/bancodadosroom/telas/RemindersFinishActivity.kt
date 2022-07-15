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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.alarm.AlarmReceiver
import com.wdretzer.bancodadosroom.alarm.AlarmRing
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.recycler.ItensAdapter
import com.wdretzer.bancodadosroom.recycler.ItensAdapterItensFinish
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel
import java.util.*

class RemindersFinishActivity : AppCompatActivity() {

    private val viewModelApp: AppViewModel by viewModels()

    private val btnHomeMenu: ShapeableImageView
        get() = findViewById(R.id.btn_send_list_today)

    private val btnAddItem: ShapeableImageView
        get() = findViewById(R.id.btn_add_list)

    private val btnDeleteAll: ShapeableImageView
        get() = findViewById(R.id.btn_delete_all)

    private val btnSearch: ShapeableImageView
        get() = findViewById(R.id.btn_send_search)

    private val textTotalItens: TextView
        get() = findViewById(R.id.total_itens_today)

    var totalItens: Int = 0
    var day = 0
    var month = 0
    var year = 0


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders_finish)

        supportActionBar?.hide()

        stopRingtoneAlarm()
        countItem()

        AlarmRing.r?.stop()
        showListTodayBD()

        btnHomeMenu.setOnClickListener { sendToListBD() }
        btnAddItem.setOnClickListener { sendToMainActivity() }
        btnSearch.setOnClickListener { sendToSearchList() }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun showListTodayBD() {
        viewModelApp.listItensItensFinish(true).observe(this) {

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_today)
            val adapter = ItensAdapterItensFinish(::updateItem, { itens ->
                showDialogDeleteItem("Deseja realmente apagar esse Lembrete?", itens)
            }) { info ->
                showDialogFinishItem("Deseja realmente finalizar esse Lembrete?", info)
            }
            adapter.updateList(it)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun updateItem(item: InfoDados) {
        sendToTelaUpdate(item)
    }

    private fun stopRingtoneAlarm() {
        val i = Intent(this, AlarmRing::class.java)
        stopService(i)
    }

    private fun sendToTelaUpdate(info: InfoDados) {
        val titulo = info.tituloInfo
        val descricao = info.descricaoInfo
        val data = info.dataInfo
        val horario = info.horarioInfo
        val id = info.idUser
        val statusLembrete = info.statusLembrete

        val intent = Intent(this, ReminderUpdateActivity::class.java).apply {
            putExtra("Titulo", titulo)
            putExtra("Desc", descricao)
            putExtra("Data", data)
            putExtra("Hora", horario)
            putExtra("Id", id)
            putExtra("Status", statusLembrete)
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
    private fun deleteItem(item: InfoDados) {
        viewModelApp.deleteItem(item).observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Lembrete deletado com sucesso!", Toast.LENGTH_SHORT).show()
                showListTodayBD()
                countItem()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun countItem() {
        viewModelApp.countAllItensFinish(true).observe(this) {
            textTotalItens.text = it.toString()
            showListTodayBD()
        }
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showDialogFinishItem(title: String, itens: InfoDados) {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.fragment_dialog_delete)

        val body = dialog.findViewById(R.id.frag_title) as TextView
        body.text = title
        val btnFinishReminder = dialog.findViewById(R.id.btn_apagar) as Button
        btnFinishReminder.text = "FINALIZAR"
        val btnCancelar = dialog.findViewById(R.id.btn_cancelar) as TextView

        btnCancelar.setOnClickListener { dialog.dismiss() }
        btnFinishReminder.setOnClickListener {
            checkItem(itens)
            resetAlarm(itens)
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun checkItem(item: InfoDados) {
        val itemNew = InfoDados(
            tituloInfo = item.tituloInfo,
            descricaoInfo = item.descricaoInfo,
            dataInfo = item.dataInfo,
            horarioInfo = item.horarioInfo,
            alarmStatusInfo = false,
            idUser = item.idUser,
            requestCode = item.requestCode,
            statusLembrete = true
        )

        viewModelApp.updateItem(itemNew).observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Os dados foram atualizados com sucesso!", Toast.LENGTH_SHORT)
                    .show()
                this.recreate()
            }

            if (it is DataResult.Error) {
                Toast.makeText(this, "Erro em atualizar os dados!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}