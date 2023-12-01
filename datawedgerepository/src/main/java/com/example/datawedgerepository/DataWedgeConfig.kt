package com.example.datawedgerepository

import android.os.Bundle
import java.util.Locale

data class DataWedgeConfig(
    val ean8: Boolean,
    val ean13: Boolean,
    val code39: Boolean,
    val code128: Boolean,
    val illuminationMode: Boolean,
    val pickListMode: Boolean
) {
    companion object {
        fun fromBundle(barcodeProps: Bundle): DataWedgeConfig {
            val ean8Enabled = barcodeProps.getString("decoder_ean8")
            val ean8 = (ean8Enabled != null && ean8Enabled.lowercase(Locale.ROOT).equals("true"))

            val ean13Enabled = barcodeProps.getString("decoder_ean13")
            val ean13 = (ean13Enabled != null && ean13Enabled.lowercase(Locale.ROOT).equals("true"))

            val code39Enabled = barcodeProps.getString("decoder_code39")
            val code39 =
                (code39Enabled != null && code39Enabled.lowercase(Locale.ROOT).equals("true"))

            val code128Enabled = barcodeProps.getString("decoder_code128")
            val code128 =
                (code128Enabled != null && code128Enabled.lowercase(Locale.ROOT).equals("true"))

            val illuminationModeEnabled = barcodeProps.getString("illumination_mode")
            val illuminationMode =
                (
                    illuminationModeEnabled != null && illuminationModeEnabled.lowercase(Locale.ROOT)
                        .equals("torch")
                    )

            val picklistModeEnabled = barcodeProps.getString("picklist")
            val pickListMode =
                (
                    picklistModeEnabled != null && !picklistModeEnabled.lowercase(Locale.ROOT)
                        .equals("0")
                    )

            return DataWedgeConfig(ean8, ean13, code39, code128, illuminationMode, pickListMode)
        }
    }
}
