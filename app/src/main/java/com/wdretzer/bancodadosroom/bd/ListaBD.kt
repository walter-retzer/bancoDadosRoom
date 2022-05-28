package com.wdretzer.bancodadosroom.bd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.recycler.ItensAdapter
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel


class ListaBD : AppCompatActivity() {

    private val viewModelApp: AppViewModel by viewModels()
    private val btn: FloatingActionButton
        get() = findViewById(R.id.fab)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_bd)

        getSupportActionBar()?.hide()

        btn.setOnClickListener {
            showFavourite()
        }
    }

    private fun showFavourite() {
        viewModelApp.getListSave().observe(this) {

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
            val adapter = ItensAdapter()
            adapter.updateList(it)

            Toast.makeText(this, "Lista Atualizada", Toast.LENGTH_SHORT).show()

            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }
}