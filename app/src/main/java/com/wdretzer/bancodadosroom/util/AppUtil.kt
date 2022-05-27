package com.wdretzer.bancodadosroom.util

import android.app.Application
import android.content.Context
import com.wdretzer.bancodadosroom.bd.DataBaseFactory


class AppUtil: Application() {

    override fun onCreate() {
        super.onCreate()
        DataBaseFactory.build(this)
        appContext = applicationContext
    }

    companion object{
        var appContext: Context? = null
    }
}
