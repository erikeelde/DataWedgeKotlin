package com.example.datawedgerepository

import android.content.Intent
import kotlinx.coroutines.flow.SharedFlow

interface DataWedgeRepository {
    val scans: SharedFlow<DataWedgeScan>
    suspend fun getScanners(): List<DataWedgeScanner>
    suspend fun getConfiguration(activeProfileName: String): DataWedgeConfig
    suspend fun getActiveProfile(): DataWedgeProfile
    suspend fun getVersions(): DataWedgeVersion
    suspend fun addScan(dataWedgeScan: DataWedgeScan)
    fun isScanIntent(intent: Intent): Boolean
    suspend fun createProfile(
        profileName: String,
        packageName: String,
        profileIntentAction: String,
        profileIntentDelivery: IntentOutputIntentDelivery
    )

    suspend fun supportsConfigCreation(): Boolean
}
