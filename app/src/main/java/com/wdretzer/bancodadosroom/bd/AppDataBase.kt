package com.wdretzer.bancodadosroom.bd

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wdretzer.bancodadosroom.dados.Converters
import com.wdretzer.bancodadosroom.dados.Dados

@Database(
    entities = [Dados::class],
    version = 1,
    exportSchema = false
)


@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun appRoomDao(): AppRoomDao
}
