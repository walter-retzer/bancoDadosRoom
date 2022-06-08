package com.wdretzer.bancodadosroom.bd

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.wdretzer.bancodadosroom.MainActivity
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.recycler.ItensAdapter
import com.wdretzer.bancodadosroom.telas.ListTodayActivity
import com.wdretzer.bancodadosroom.telas.SearchListActivity
import com.wdretzer.bancodadosroom.telas.TelaUpdateDados
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel


class ListaBD : AppCompatActivity() {

    private val viewModelApp: AppViewModel by viewModels()

    private val btnAddItem: ShapeableImageView
        get() = findViewById(R.id.btn_add_list)

    private val btnSendListToday: ShapeableImageView
        get() = findViewById(R.id.btn_send_list_today)

    private val btnSendSearch: ShapeableImageView
        get() = findViewById(R.id.btn_send_search)

    private val btnDeleteAll: ShapeableImageView
        get() = findViewById(R.id.btn_delete_all)

    private val textTotalItens: TextView
        get() = findViewById(R.id.total_itens)

    var totalItens: Int = 0
    var day = 0
    var month = 0
    var year = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_bd)

        getSupportActionBar()?.hide()
        showListBD()
        btnAddItem.setOnClickListener { sendToMainActivity() }
        btnSendListToday.setOnClickListener { sendToListToday() }
        btnSendSearch.setOnClickListener { sendToSearchList() }
        btnDeleteAll.setOnClickListener { showDialog("Deseja realmente apagar todos os Lembrete?") }
    }


    private fun showListBD() {
        viewModelApp.getListSave().observe(this) {

            textTotalItens.text = it.size.toString()

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
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


    private fun deleteItem(item: InfoDados) {
        viewModelApp.deleteItem(item).observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Delete Sucess!", Toast.LENGTH_SHORT).show()
                showListBD()
            }
        }
    }


    private fun deleteAll() {
        viewModelApp.deleteAll().observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Delete All Sucess!", Toast.LENGTH_SHORT).show()
                showListBD()
            }
        }
    }


    private fun sendToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    private fun sendToListToday() {
        val intent = Intent(this, ListTodayActivity::class.java)
        startActivity(intent)
    }

    private fun sendToSearchList() {
        val intent = Intent(this, SearchListActivity::class.java)
        startActivity(intent)
    }


    private fun showDialog(title: String) {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.fragment_dialog_delete)

        val body = dialog.findViewById(R.id.frag_title) as TextView
        body.text = title
        val btnApagar = dialog.findViewById(R.id.btn_apagar) as Button
        val btnCancelar = dialog.findViewById(R.id.btn_cancelar) as TextView

        btnApagar.setOnClickListener {
            deleteAll()
            dialog.dismiss()
        }
        btnCancelar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
