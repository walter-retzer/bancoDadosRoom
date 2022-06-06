package com.wdretzer.bancodadosroom.bd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wdretzer.bancodadosroom.MainActivity
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.recycler.ItensAdapter
import com.wdretzer.bancodadosroom.telas.TelaUpdateDados
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel


class ListaBD : AppCompatActivity() {

    private val viewModelApp: AppViewModel by viewModels()
    private val btn: FloatingActionButton
        get() = findViewById(R.id.fab)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_bd)

        getSupportActionBar()?.hide()

        showFavourite()

        btn.setOnClickListener {
            sendToMainActivity()
        }
    }

    private fun showFavourite() {
        viewModelApp.getListSave().observe(this) {

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
            val adapter = ItensAdapter(::updateItem) {
                deleteItem(it)
            }

            adapter.updateList(it)

            Toast.makeText(this, "Lista Atualizada", Toast.LENGTH_SHORT).show()

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
                showFavourite()
            }
        }
    }

    private fun sendToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}