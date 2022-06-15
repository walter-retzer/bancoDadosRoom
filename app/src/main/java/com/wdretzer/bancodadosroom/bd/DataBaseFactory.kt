package com.wdretzer.bancodadosroom.bd

import android.content.Context
import androidx.room.Room

object DataBaseFactory {

    private var instance: AppDataBase? = null

    fun getDataBase() = instance ?: throw IllegalStateException("Database is not initialized")

    fun build(context: Context): AppDataBase {

        val currentInstance = instance
        if (currentInstance != null) return currentInstance

        val dataBase = Room.databaseBuilder(
            context.applicationContext,
            AppDataBase::class.java,
            "BD-Room"
        )

        dataBase.allowMainThreadQueries()
        val dbInstance = dataBase.build()
        instance = dbInstance

        return dbInstance
    }

    fun removeInstance(){
        instance = null
    }
}
