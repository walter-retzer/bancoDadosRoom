package com.wdretzer.bancodadosroom.bd

import androidx.room.*
import androidx.room.Query
import com.wdretzer.bancodadosroom.dados.Dados


@Dao
interface AppRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg word: Dados)

    @Query("DELETE FROM infoDB")
    fun deleteAll()

    @Query("SELECT * FROM infoDB")
    fun listAll(): List<Dados>

    @Query("SELECT * FROM infoDB WHERE data = :apiData")
    fun listItensToday(apiData: String): List<Dados>

    @Query("SELECT * FROM infoDB WHERE data = :apiData AND statusLembrete = :apiLembrete")
    fun listItensTodayFinish(apiData: String, apiLembrete: Boolean): List<Dados>

    @Query("SELECT COUNT(titulo) FROM infoDB WHERE data = :apiData")
    fun countApiId(apiData: String): Int

    @Query("SELECT COUNT(horario) FROM infoDB WHERE horario = :apiData")
    fun countApiIdTime(apiData: String): Int

    @Query("SELECT COUNT(titulo) FROM infoDB WHERE statusLembrete = :apiLembrete AND data = :apiData")
    fun countApiIdFinish(apiData: String, apiLembrete: Boolean): Int

    @Query("UPDATE infoDB SET titulo = :itemNew, descricao = :desc, data = :dia, horario = :hora, alarme = :statusAlarm, statusLembrete = :statusLembrete WHERE id = :id")
    fun updateAll(
        id: Int,
        itemNew: String,
        desc: String,
        dia: String,
        hora: String,
        statusAlarm: Boolean,
        statusLembrete: Boolean
    )

    @Query("DELETE FROM infoDB WHERE id = :id")
    fun deleteByApiId(id: Int)
}
