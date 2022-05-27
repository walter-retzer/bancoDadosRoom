package com.wdretzer.bancodadosroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import com.wdretzer.bancodadosroom.bd.ListaBD
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel


class MainActivity : AppCompatActivity() {

    private val viewModelNasa: AppViewModel by viewModels()

    private val btnCadastrar: Button
        get() = findViewById(R.id.btn_continue)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportActionBar()?.hide()
        saveFavourite()

        btnCadastrar.setOnClickListener {
            val intent = Intent(this, ListaBD::class.java)
            startActivity(intent)
            //saveFavourite()
        }
    }

    private fun saveFavourite() {
        viewModelNasa.addOrRemoveFavourite("Top das Galáxias", "12h50min").observe(this) {
            if (it is DataResult.Success) {

                Toast.makeText(this, "Sucesso $it", Toast.LENGTH_SHORT).show()

            }

            if (it is DataResult.Error) {

                Toast.makeText(this, "Error $it", Toast.LENGTH_SHORT).show()

            }
        }
    }
}