package com.example.datawedgerepository

import android.os.Bundle

data class DataWedgeVersion(
    val barcodeScanningVersion: String,
    val dataWedgeVersion: String,
    val decoderLibraryVersion: String,
    val scannerFirmwareVersion: Array<String>?,
    val simulScanVersion: String?,
) {
    companion object {
        fun fromBundle(bundle: Bundle): DataWedgeVersion {
            val barcodeScanningVersion = bundle.getString("BARCODE_SCANNING", null)!!
            val dataWedgeVersion =
                bundle.getString(DWInterface.DATAWEDGE_RETURN_VERSION_DATAWEDGE, null)!!
            val decoderLibraryVersion = bundle.getString("DECODER_LIBRARY", null)!!
            val scannerFirmwareVersion: Array<String>? = bundle.getStringArray("SCANNER_FIRMWARE")
            val simulScanVersion = bundle.getString("SIMULSCAN", null)
            return DataWedgeVersion(
                barcodeScanningVersion = barcodeScanningVersion,
                dataWedgeVersion = dataWedgeVersion,
                decoderLibraryVersion = decoderLibraryVersion,
                scannerFirmwareVersion = scannerFirmwareVersion,
                simulScanVersion = simulScanVersion
            )
        }
    }
}
