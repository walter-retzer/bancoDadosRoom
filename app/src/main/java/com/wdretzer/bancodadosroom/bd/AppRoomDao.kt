package com.wdretzer.bancodadosroom.bd

import androidx.room.*
import androidx.room.Query
import com.wdretzer.bancodadosroom.dados.Dados
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.NotNull


@Dao
interface AppRoomDao {

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insert(word: Dados)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg word: Dados)

    @NotNull
    @Query("SELECT * FROM infoDB")
    fun listAll(): List<Dados>

}


//    @Insert(onConflict = OnConflictStrategy.REPLACE )
//    fun insertAll(appEntity: List<AppRoomEntity>)
//
//    @Delete
//    fun delete(vararg appEntity: AppRoomEntity)

//    @Query("DELETE FROM infoDB")
//    fun deleteAll()
//
//    @Query("DELETE FROM infoDB WHERE item = :item")
//    fun deleteItemId(item: Int?)
//
//    @Update
//    fun update(vararg appEntity: AppRoomEntity)


