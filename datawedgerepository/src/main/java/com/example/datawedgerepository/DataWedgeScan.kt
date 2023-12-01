package com.example.datawedgerepository

import android.content.Intent
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class DataWedgeScan(
    val data: String,
    val symbology: String,
    val scannedInstant: Instant,
) {
    companion object {
        fun fromIntent(intent: Intent): DataWedgeScan {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)!!
            val symbology = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_LABEL_TYPE)!!
            val scannedInstant = Clock.System.now()
            return DataWedgeScan(scanData, symbology, scannedInstant)
        }
    }
}
