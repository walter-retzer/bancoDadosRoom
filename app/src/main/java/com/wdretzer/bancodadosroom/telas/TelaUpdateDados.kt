
package com.wdretzer.bancodadosroom.telas

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

class TelaUpdateDados : AppCompatActivity() {

    private val viewModelApp: AppViewModel by viewModels()

    private val btnCadastrar: Button
        get() = findViewById(R.id.btn_continue)

    private val textTitulo: TextInputEditText
        get() = findViewById(R.id.titulo_input_text)

    private val textDescricao: TextInputEditText
        get() = findViewById(R.id.descricao_input_text)

    private val dataLayout: TextInputLayout
        get() = findViewById(R.id.data_input_layout)

    private val textData: TextInputEditText
        get() = findViewById(R.id.data_input_text)

    private val textHorario: TextInputEditText
        get() = findViewById(R.id.horario_input_text)

    private val horaLayout: TextInputLayout
        get() = findViewById(R.id.horario_input_layout)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_update_dados)

        val bundle: Bundle? = intent.extras
        var idBundle: Int = 0

        if (bundle != null) {
            val setTextTitulo = bundle.getString("Titulo")
            val setDesc = bundle.getString("Desc")
            val setData = bundle.getString("Data")
            val setHora = bundle.getString("Hora")
            val setId = bundle.getInt("Id")

            setTextTitulo?.let { textTitulo.setText(it) }
            setDesc?.let { textDescricao.setText(it) }
            setData?.let { textData.setText(it) }
            setHora?.let { textHorario.setText(it) }
            idBundle = setId
        }

        getSupportActionBar()?.hide()

        val myCalendar = Calendar.getInstance()

        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }

        dataLayout.setOnClickListener {
            DatePickerDialog(
                this,
                datePickerOnDataSetListener,
                myCalendar.get(Calendar.DAY_OF_MONTH),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.YEAR)
            ).show()
        }

        horaLayout.setOnClickListener {
            DatePickerDialog(
                this,
                datePickerOnDataSetListener,
                myCalendar.get(Calendar.DAY_OF_MONTH),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.YEAR)
            ).show()
        }

        btnCadastrar.setOnClickListener {
            DatePickerDialog(
                this,
                datePickerOnDataSetListener,
                myCalendar.get(Calendar.DAY_OF_MONTH),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.YEAR)
            ).show()

//            updateItem(
//                InfoDados(
//                    textTitulo.text.toString(),
//                    textDescricao.text.toString(),
//                    textData.text.toString(),
//                    textHorario.text.toString(),
//                    idBundle
//                )
//            )
        }
    }

    private fun setDatePicker(dateEditText: EditText) {

        val myCalendar = Calendar.getInstance()

        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel(myCalendar, dateEditText)
            }

        dateEditText.setOnClickListener {
            DatePickerDialog(
                this@TelaUpdateDados, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateLabel(myCalendar: Calendar, dateEditText: EditText) {
        val myFormat: String = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        dateEditText.setText(sdf.format(myCalendar.time))
    }

    private fun updateItem(item: InfoDados) {
        viewModelApp.updateItem(item).observe(this) {

            if (it is DataResult.Success) {
                Toast.makeText(this, "Update Sucess!", Toast.LENGTH_SHORT).show()
            }

            if (it is DataResult.Error) {
                Toast.makeText(this, "Update Error!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}