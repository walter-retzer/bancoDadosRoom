package com.wdretzer.bancodadosroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.wdretzer.bancodadosroom.bd.ListaBD
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel


class MainActivity : AppCompatActivity() {

    private val viewModelApp: AppViewModel by viewModels()

    private val btnCadastrar: Button
        get() = findViewById(R.id.btn_continue)

    private val textTitulo: TextInputEditText
        get() = findViewById(R.id.titulo_input_text)

    private val textDescricao: TextInputEditText
        get() = findViewById(R.id.descricao_input_text)

    private val textData: TextInputEditText
        get() = findViewById(R.id.data_input_text)

    private val textHorario: TextInputEditText
        get() = findViewById(R.id.horario_input_text)

    var titulo: String = " "
    var descricao: String = " "
    var data: String = " "
    var horario: String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportActionBar()?.hide()

        btnCadastrar.setOnClickListener {
            saveFavourite()
            sendToListaBD()
        }
    }

    private fun saveFavourite() {
        titulo = textTitulo.text.toString()
        descricao = textDescricao.text.toString()
        data = textData.text.toString()
        horario = textHorario.text.toString()

        viewModelApp.addOrRemoveItens(titulo, descricao, data, horario).observe(this) {
            if (it is DataResult.Success) {
                Toast.makeText(this, "Dados Salvos no Banco de Dados!", Toast.LENGTH_SHORT).show()
            }

            if (it is DataResult.Error) {
                Toast.makeText(this, "Error $it ao Salvar os Dados!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendToListaBD() {
        Handler().postDelayed({
            val intent = Intent(this, ListaBD::class.java)
            startActivity(intent)
        }, 2000)
    }
}
