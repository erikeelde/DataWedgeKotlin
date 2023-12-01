package com.example.datawedgerepository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf

class DWInterface(private val applicationContext: Context) {
    companion object {
        const val DATAWEDGE_SEND_ACTION = "com.symbol.datawedge.api.ACTION"
        const val DATAWEDGE_RETURN_ACTION = "com.symbol.datawedge.api.RESULT_ACTION"
        const val DATAWEDGE_RETURN_CATEGORY = "android.intent.category.DEFAULT"
        const val DATAWEDGE_EXTRA_SEND_RESULT = "SEND_RESULT"
        const val DATAWEDGE_EXTRA_RESULT = "RESULT"
        const val DATAWEDGE_EXTRA_COMMAND = "COMMAND"
        const val DATAWEDGE_EXTRA_RESULT_INFO = "RESULT_INFO"
        const val DATAWEDGE_EXTRA_RESULT_CODE = "RESULT_CODE"

        const val DATAWEDGE_SCAN_EXTRA_DATA_STRING = "com.symbol.datawedge.data_string"
        const val DATAWEDGE_SCAN_EXTRA_LABEL_TYPE = "com.symbol.datawedge.label_type"

        const val DATAWEDGE_SEND_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE"

        const val DATAWEDGE_SEND_GET_VERSION = "com.symbol.datawedge.api.GET_VERSION_INFO"
        const val DATAWEDGE_RETURN_VERSION = "com.symbol.datawedge.api.RESULT_GET_VERSION_INFO"
        const val DATAWEDGE_RETURN_VERSION_DATAWEDGE = "DATAWEDGE"

        const val DATAWEDGE_SEND_GET_ENUMERATE_SCANNERS =
            "com.symbol.datawedge.api.ENUMERATE_SCANNERS"
        const val DATAWEDGE_RETURN_ENUMERATE_SCANNERS =
            "com.symbol.datawedge.api.RESULT_ENUMERATE_SCANNERS"

        const val DATAWEDGE_SEND_GET_CONFIG = "com.symbol.datawedge.api.GET_CONFIG"
        const val DATAWEDGE_RETURN_GET_CONFIG = "com.symbol.datawedge.api.RESULT_GET_CONFIG"
        const val DATAWEDGE_SEND_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG"

        const val DATAWEDGE_SEND_GET_ACTIVE_PROFILE = "com.symbol.datawedge.api.GET_ACTIVE_PROFILE"
        const val DATAWEDGE_RETURN_GET_ACTIVE_PROFILE =
            "com.symbol.datawedge.api.RESULT_GET_ACTIVE_PROFILE"

        const val DATAWEDGE_SEND_SWITCH_SCANNER = "com.symbol.datawedge.api.SWITCH_SCANNER"

        const val DATAWEDGE_SEND_SET_SCANNER_INPUT = "com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN"
        const val DATAWEDGE_SEND_SET_SCANNER_INPUT_ENABLE = "ENABLE_PLUGIN"
        const val DATAWEDGE_SEND_SET_SCANNER_INPUT_DISABLE = "DISABLE_PLUGIN"

        const val DATAWEDGE_SEND_SET_SOFT_SCAN = "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER"
    }

    fun sendCommandString(
        command: String,
        parameter: String,
        sendResult: Boolean = false
    ) {
        val dwIntent = Intent()
        dwIntent.action = DATAWEDGE_SEND_ACTION
        dwIntent.putExtra(command, parameter)
        if (sendResult)
            dwIntent.putExtra(DATAWEDGE_EXTRA_SEND_RESULT, "true")
        applicationContext.sendBroadcast(dwIntent)
    }

    fun sendCommandBundle(command: String, parameter: Bundle) {
        val dwIntent = Intent()
        dwIntent.action = DATAWEDGE_SEND_ACTION
        dwIntent.putExtra(command, parameter)
        applicationContext.sendBroadcast(dwIntent)
    }

    fun setConfigForDecoder(
        profileName: String,
        ean8Value: Boolean,
        ean13Value: Boolean,
        code39Value: Boolean,
        code128Value: Boolean,
        illuminationValue: String,
        picklistModeValue: String
    ) {
        val profileConfig = bundleOf(
            "PROFILE_NAME" to profileName,
            "PROFILE_ENABLED" to "true",
            "CONFIG_MODE" to "UPDATE",
            "PLUGIN_CONFIG" to bundleOf(
                "PLUGIN_NAME" to "BARCODE",
                "RESET_CONFIG" to "true",
                "PARAM_LIST" to bundleOf(
                    "scanner_selection" to "auto",
                    "decoder_ean8" to ean8Value.toString(),
                    "decoder_ean13" to ean13Value.toString(),
                    "decoder_code39" to code39Value.toString(),
                    "decoder_code128" to code128Value.toString(),
                    "illumination_mode" to illuminationValue,
                    "picklist" to picklistModeValue,
                )
            )
        )

        sendCommandBundle(DATAWEDGE_SEND_SET_CONFIG, profileConfig)
    }

    fun startScan() {
        sendCommandString(
            DATAWEDGE_SEND_SET_SOFT_SCAN,
            "START_SCANNING"
        )
    }

    fun stopScan() {
        sendCommandString(
            DATAWEDGE_SEND_SET_SOFT_SCAN,
            "STOP_SCANNING"
        )
    }

    fun requestCurrentConfiguration(profileName: String) {
        //  Request the current configuration.  For brevity / readability I have not specified the
        //  bundle keys as constants in DWInterface
        val bundle = bundleOf(
            "PROFILE_NAME" to profileName,
            "PLUGIN_CONFIG" to bundleOf(
                "PLUGIN_NAME" to arrayListOf("BARCODE")
            )
        )

        sendCommandBundle(DATAWEDGE_SEND_GET_CONFIG, bundle)
    }

    fun disableScannerInput() {
        sendCommandString(
            DATAWEDGE_SEND_SET_SCANNER_INPUT,
            DATAWEDGE_SEND_SET_SCANNER_INPUT_DISABLE
        )
    }

    fun enableScannerInput() {
        sendCommandString(
            DATAWEDGE_SEND_SET_SCANNER_INPUT,
            DATAWEDGE_SEND_SET_SCANNER_INPUT_ENABLE
        )
    }

    fun setActiveScanner(scannerIndex: Int) {
        sendCommandString(
            DATAWEDGE_SEND_SWITCH_SCANNER, "" + scannerIndex,
            sendResult = true
        )
    }
}