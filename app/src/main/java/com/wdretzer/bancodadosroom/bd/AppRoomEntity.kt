package com.wdretzer.bancodadosroom.bd

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "infoDXXXX")
data class AppRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val item: Int = 0,
    @ColumnInfo
    val titulo:String = " ",
    @ColumnInfo
    val descricao:String = " ",
    @ColumnInfo
    val date:String = " ",
)

