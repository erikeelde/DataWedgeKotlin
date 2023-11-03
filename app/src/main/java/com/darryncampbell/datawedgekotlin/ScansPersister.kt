package com.darryncampbell.datawedgekotlin

import android.app.Activity
import android.content.Context
import com.example.datawedgerepository.DataWedgeScan
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileNotFoundException
import okio.buffer
import okio.sink
import okio.source

private const val SCAN_HISTORY_FILE_NAME = "ScanHistory"

class ScansPersister(private val applicationContext: Context) {
    suspend fun readScans(): List<DataWedgeScan> {
        try {
            val bufferedSource = applicationContext
                .openFileInput(SCAN_HISTORY_FILE_NAME).source().buffer()

            val string = bufferedSource.readUtf8()
            return Json.decodeFromString<List<DataWedgeScan>>(string)
        } catch (fileNotFoundException: FileNotFoundException) {
            return listOf()
        }
    }

    suspend fun writeScans(dataWedgeScans: List<DataWedgeScan>) {
        val string = Json.encodeToString(dataWedgeScans)
        val bufferedSink =
            applicationContext
                .openFileOutput(SCAN_HISTORY_FILE_NAME, Activity.MODE_PRIVATE)
                .sink()
                .buffer()
        bufferedSink.writeUtf8(string)
        bufferedSink.flush()
    }

    suspend fun clear() {
        applicationContext.getFileStreamPath(SCAN_HISTORY_FILE_NAME).delete()
    }
}