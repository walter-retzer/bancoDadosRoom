package com.wdretzer.bancodadosroom.bd

import androidx.room.*
import androidx.room.Query
import com.wdretzer.bancodadosroom.dados.Dados


@Dao
interface AppRoomDao {

    // Todas as Funções foram testadas:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg word: Dados)

    @Query("DELETE FROM infoDB")
    fun deleteAll()

    @Query("SELECT * FROM infoDB")
    fun listAll(): List<Dados>

    @Query("SELECT COUNT(titulo) FROM infoDB WHERE data = :apiData")
    fun countApiId(vararg apiData: String): Int

    @Query("UPDATE infoDB SET titulo = :itemNew, descricao = :desc, data = :dia, horario = :hora WHERE id = :id")
    fun updateAll(id: Int, itemNew: String, desc: String, dia: String, hora: String)

    @Query("DELETE FROM infoDB WHERE id = :id")
    fun deleteByApiId(vararg id: Int)
}
