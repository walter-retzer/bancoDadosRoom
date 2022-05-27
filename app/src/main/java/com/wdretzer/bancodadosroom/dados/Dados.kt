package com.wdretzer.bancodadosroom.dados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(tableName = "infoDB")
data class Dados(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    val titulo: String = "texto padrão",
    @ColumnInfo
    val horario:String = "00h00min",
)


// Classe TypeConvert para converter as listas para dentro do Banco de Dados do Room
class Converters {

    // Funções para converter as Listas da Classe Itens Data
    @TypeConverter
    fun listToJsonString(value: List<Dados>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(value: String) = Gson().fromJson(value, Array<Dados>::class.java).toList()

}
