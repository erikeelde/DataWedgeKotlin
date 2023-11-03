package com.example.datawedgerepository

import android.os.Bundle

data class DataWedgeScanner(
    val scannerConnectionState: Boolean,
    val scannerIdentifier: String,
    val scannerName: String,
    val scannerIndex: Int
) {
    companion object {
        fun fromBundle(bundle: Bundle): DataWedgeScanner {
            val scannerConnectionState = bundle.getBoolean("SCANNER_CONNECTION_STATE", false)
            val scannerIdentifier: String = bundle.getString("SCANNER_IDENTIFIER", null)
            val scannerName: String = bundle.getString("SCANNER_NAME", null)
            val scannerIndex = bundle.getInt("SCANNER_INDEX", -1)

            return DataWedgeScanner(
                scannerConnectionState,
                scannerIdentifier,
                scannerName,
                scannerIndex
            )

        }
    }
}