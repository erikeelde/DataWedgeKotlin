package com.example.datawedgerepository

import android.os.Bundle
import androidx.core.os.bundleOf

data class BarcodeInputConfiguration(
    val enabled: Boolean,
) {
    // https://techdocs.zebra.com/datawedge/latest/guide/api/setconfig/#scannerinputparameters
    fun asBundle(): Bundle = bundleOf(
        "PLUGIN_NAME" to "BARCODE",
        "PARAM_LIST" to bundleOf(
            "scanner_input_enabled" to enabled.toString(),
            "barcode_trigger_mode" to true.toString()
        )
    )
}