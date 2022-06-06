package com.wdretzer.bancodadosroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
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

    private val loading: FrameLayout
        get() = findViewById(R.id.loading)


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

        viewModelApp.addOrRemoveItens(

            textTitulo.text.toString(),
            textDescricao.text.toString(),
            textData.text.toString(),
            textHorario.text.toString()

        ).observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Dados Salvos no Banco de Dados!", Toast.LENGTH_SHORT).show()
            }

            if (it is DataResult.Error) {
                Toast.makeText(this, "Error ao Salvar os Dados!", Toast.LENGTH_SHORT).show()
            }
//
//            if (it is DataResult.Loading) {
//                loading.isVisible = it.isLoading
//            }
        }
    }

    private fun sendToListaBD() {
        loading.isVisible = true
        Handler().postDelayed({
            loading.isVisible = false
            val intent = Intent(this, ListaBD::class.java)
            startActivity(intent)
        }, 3000)
    }
}
