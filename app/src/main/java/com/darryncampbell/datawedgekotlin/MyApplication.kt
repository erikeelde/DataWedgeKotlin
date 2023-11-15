package com.darryncampbell.datawedgekotlin

import android.app.Application
import com.example.datawedgerepository.DataWedgeRepository
import com.example.datawedgerepository.DataWedgeRepositoryImpl
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
        dataWedgeRepository = DataWedgeRepositoryImpl(this.applicationContext)
    }
}

private lateinit var dataWedgeRepository: DataWedgeRepository
fun DataWedgeRepository.Companion.getInstance(): DataWedgeRepository {
    return dataWedgeRepository
}