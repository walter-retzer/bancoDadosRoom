package com.wdretzer.bancodadosroom.telas

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.wdretzer.bancodadosroom.MainActivity
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.bd.ListaBD
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.recycler.ItensAdapter
import com.wdretzer.bancodadosroom.recycler.ListTodayAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_today)

        getSupportActionBar()?.hide()

        getDateCalendar()
        countItem("$day/$month/$year")
        showListTodayBD("$day/$month/$year")

        btnHomeMenu.setOnClickListener { sendToListBD() }
        btnAddItem.setOnClickListener { sendToMainActivity() }
        btnSearch.setOnClickListener { sendToMainActivity() }

    }


    override fun onBackPressed() {
        val intent = Intent(this, ListaBD::class.java)
        startActivity(intent)
    }


    private fun showListTodayBD(data: String) {
        viewModelApp.listItensToday(data).observe(this) {

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_today)
            val adapter = ItensAdapter(::updateItem) {
                deleteItem(it)
            }

            adapter.updateList(it)

            Toast.makeText(this, "Lista Atualizada!", Toast.LENGTH_SHORT).show()

            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }


    private fun updateItem(item: InfoDados) {
        sendToTelaUpdate(item)
    }


    private fun deleteItem(item: InfoDados) {
        viewModelApp.deleteItem(item).observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Delete Sucess!", Toast.LENGTH_SHORT).show()
                showListTodayBD("$day/$month/$year")
                countItem("$day/$month/$year")
            }
        }
    }


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

        val intent = Intent(this, TelaUpdateDados::class.java).apply {
            putExtra("Titulo", titulo)
            putExtra("Desc", descricao)
            putExtra("Data", data)
            putExtra("Hora", horario)
            putExtra("Id", id)
        }
        startActivity(intent)
    }


    private fun sendToListBD() {
        val intent = Intent(this, ListaBD::class.java)
        startActivity(intent)
    }

    private fun sendToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}
