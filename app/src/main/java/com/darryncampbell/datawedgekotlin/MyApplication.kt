package com.darryncampbell.datawedgekotlin

import android.app.Application
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
        DataWedgeHolder.init(this.applicationContext)
    }
}
