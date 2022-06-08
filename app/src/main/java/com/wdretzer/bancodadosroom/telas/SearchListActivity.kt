package com.wdretzer.bancodadosroom.telas

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wdretzer.bancodadosroom.MainActivity
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.bd.ListaBD
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.recycler.ItensAdapter
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel
import java.util.*


class SearchListActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val viewModelApp: AppViewModel by viewModels()

    private val dataSearch: ImageView
        get() = findViewById(R.id.imagem_data_search)

    private val buttonSearch: ImageView
        get() = findViewById(R.id.button_search)

    private val buttonSendHome: ImageView
        get() = findViewById(R.id.btn_home_search)

    private val buttonSendListToday: ImageView
        get() = findViewById(R.id.btn_today_search)

    private val buttonSendAddList: ImageView
        get() = findViewById(R.id.btn_add_list_search)

    private val textDataSearch: TextView
        get() = findViewById(R.id.text_data_search)

    private val textTotalItens: TextView
        get() = findViewById(R.id.total_itens_search)

    var day = 0
    var month = 0
    var year = 0
    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_list)

        getSupportActionBar()?.hide()
        getDateCalendar()

        dataSearch.setOnClickListener { pickDate() }
        buttonSendListToday.setOnClickListener { sendToListToday() }
        buttonSendAddList.setOnClickListener { sendToAddList() }
        buttonSendHome.setOnClickListener { sendToHome() }

        buttonSearch.setOnClickListener {
            if (textDataSearch.text == "Data: ") {
                Toast.makeText(this, "Selecione uma data válida!", Toast.LENGTH_SHORT).show()
            } else {
                countItem("$savedDay/$savedMonth/$savedYear")
                showListSearchBD("$savedDay/$savedMonth/$savedYear")
            }
        }
    }


    private fun showListSearchBD(data: String) {
        viewModelApp.listItensToday(data).observe(this) {

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_search)
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


    private fun countItem(item: String) {
        viewModelApp.countItens(item).observe(this) {
            textTotalItens.text = it.toString()
            if (it.toString() == "0") {
                Toast.makeText(this, "Nenhum Lembrete encontrado!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteItem(item: InfoDados) {
        viewModelApp.deleteItem(item).observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Delete Sucess!", Toast.LENGTH_SHORT).show()
                countItem("$savedDay/$savedMonth/$savedYear")
                showListSearchBD("$savedDay/$savedMonth/$savedYear")
            }
        }
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


    private fun getDateCalendar() {
        val myCalendar = Calendar.getInstance()
        day = myCalendar.get(Calendar.DAY_OF_MONTH)
        month = myCalendar.get(Calendar.MONTH)
        year = myCalendar.get(Calendar.YEAR)
    }


    private fun pickDate() {
        getDateCalendar()
        DatePickerDialog(this, this, year, month, day).show()
    }


    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month + 1
        savedYear = year

        textDataSearch.text = "$savedDay/$savedMonth/$savedYear"
    }

    private fun sendToAddList() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    private fun sendToListToday() {
        val intent = Intent(this, ListTodayActivity::class.java)
        startActivity(intent)
    }

    private fun sendToHome() {
        val intent = Intent(this, ListaBD::class.java)
        startActivity(intent)
    }
}



