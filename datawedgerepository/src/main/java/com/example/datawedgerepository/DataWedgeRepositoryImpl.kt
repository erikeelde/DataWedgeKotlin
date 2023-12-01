package com.example.datawedgerepository

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import io.github.z4kn4fein.semver.toVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import logcat.logcat

private const val DEFAULT_TIMEOUT = 3000L
private const val RECEIVED_SCANS_BUFFER = 2

@Suppress("TooManyFunctions")
class DataWedgeRepositoryImpl(applicationContext: Context) : DataWedgeRepository {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val receiver = DataWedgeRepositoryReceiver(scope)

    private val dwInterface = DWInterface(applicationContext)

    private val receivedScansChannel: Channel<DataWedgeScan> = Channel(RECEIVED_SCANS_BUFFER)
    override val scans = receivedScansChannel.consumeAsFlow()
        .shareIn(scope, SharingStarted.Eagerly, 0)

    init {
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        ContextCompat.registerReceiver(
            applicationContext,
            receiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )

        scope.launch {
            receiver.intents.collect { receivedIntent ->
                logcat { "Got intent" }
                if (receivedIntent.hasExtra(DWInterface.DATAWEDGE_EXTRA_RESULT) &&
                    receivedIntent.hasExtra(DWInterface.DATAWEDGE_EXTRA_COMMAND)
                ) {
                    logcat { "Result + command" }
                }

                if (receivedIntent.hasExtra(DWInterface.DATAWEDGE_RETURN_GET_ACTIVE_PROFILE)) {
                    val profile = parseDataWedgeProfile(receivedIntent)
                    logcat { "Active Profile + $profile" }
                } else if (receivedIntent.hasExtra(DWInterface.DATAWEDGE_RETURN_ENUMERATE_SCANNERS)) {
                    val scanners = parseEnumerateScanners(receivedIntent)
                    logcat { "Enumerate Scanners + $scanners" }
                } else if (receivedIntent.hasExtra(DWInterface.DATAWEDGE_RETURN_GET_CONFIG)) {
                    val config = parseDataWedgeConfig(receivedIntent)
                    logcat { "Get config $config" }
                } else if (receivedIntent.hasExtra(DWInterface.DATAWEDGE_RETURN_VERSION)) {
                    val version = parseDataWedgeVersion(receivedIntent)
                    logcat { "Get version $version" }
                } else {
                    logcat { "Not sure ${receivedIntent.extras}" }
                }
            }
        }
    }

    override fun isScanIntent(intent: Intent): Boolean =
        intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)

    override suspend fun addScan(dataWedgeScan: DataWedgeScan) {
        receivedScansChannel.send(dataWedgeScan)
    }

    override suspend fun getVersions(): DataWedgeVersion {
        return withTimeout(DEFAULT_TIMEOUT) {
            dwInterface.sendCommandString(DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            receiver.intents
                .filter { it.hasExtra(DWInterface.DATAWEDGE_RETURN_VERSION) }
                .map { parseDataWedgeVersion(it) }
                .first()
        }
    }

    override suspend fun getActiveProfile(): DataWedgeProfile {
        return withTimeout(DEFAULT_TIMEOUT) {
            dwInterface.sendCommandString(DWInterface.DATAWEDGE_SEND_GET_ACTIVE_PROFILE, "")
            receiver.intents
                .filter { it.hasExtra(DWInterface.DATAWEDGE_RETURN_GET_ACTIVE_PROFILE) }
                .map { parseDataWedgeProfile(it) }
                .first()
        }
    }

    override suspend fun getConfiguration(activeProfileName: String): DataWedgeConfig {
        return withTimeout(DEFAULT_TIMEOUT) {
            val bMain = bundleOf(
                "PROFILE_NAME" to activeProfileName,
                "PLUGIN_CONFIG" to bundleOf(
                    "PLUGIN_NAME" to arrayListOf("BARCODE")
                )
            )

            dwInterface.sendCommandBundle(DWInterface.DATAWEDGE_SEND_GET_CONFIG, bMain)
            receiver.intents
                .filter { it.hasExtra(DWInterface.DATAWEDGE_RETURN_GET_CONFIG) }
                .map { parseDataWedgeConfig(it) }
                .first()
        }
    }

    override suspend fun getScanners(): List<DataWedgeScanner> {
        return withTimeout(DEFAULT_TIMEOUT) {
            dwInterface.sendCommandString(
                DWInterface.DATAWEDGE_SEND_GET_ENUMERATE_SCANNERS,
                ""
            )
            receiver.intents
                .filter { it.hasExtra(DWInterface.DATAWEDGE_RETURN_ENUMERATE_SCANNERS) }
                .map { parseEnumerateScanners(it) }
                .first()
        }
    }

    private fun parseDataWedgeVersion(receivedIntent: Intent): DataWedgeVersion {
        val versionBundle =
            receivedIntent.getBundleExtra(DWInterface.DATAWEDGE_RETURN_VERSION)!!
        return DataWedgeVersion.fromBundle(versionBundle)
    }

    private fun parseEnumerateScanners(receivedIntent: Intent): List<DataWedgeScanner> {
        return try {
            val enumeratedScanners: ArrayList<Bundle> =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    receivedIntent
                        .getSerializableExtra(
                            DWInterface.DATAWEDGE_RETURN_ENUMERATE_SCANNERS,
                            ArrayList::class.java
                        ) as ArrayList<Bundle>? ?: arrayListOf()
                } else {
                    receivedIntent
                        .getSerializableExtra(
                            DWInterface.DATAWEDGE_RETURN_ENUMERATE_SCANNERS
                        ) as ArrayList<Bundle>? ?: arrayListOf()
                }
            return enumeratedScanners.map {
                DataWedgeScanner.fromBundle(it)
            }
        } catch (e: ClassCastException) {
            logcat { e.stackTraceToString() }
            listOf()
        }
    }

    private fun parseDataWedgeProfile(receivedIntent: Intent): DataWedgeProfile {
        val profileName =
            receivedIntent.getStringExtra(DWInterface.DATAWEDGE_RETURN_GET_ACTIVE_PROFILE)!!
        return DataWedgeProfile(profileName)
    }

    private fun parseDataWedgeConfig(receivedIntent: Intent): DataWedgeConfig {
        //  For readability, I have kept the bundle keys as Strings rather than constants.
        val configurationBundle =
            receivedIntent.getBundleExtra(DWInterface.DATAWEDGE_RETURN_GET_CONFIG)!!
        val pluginConfig =
            configurationBundle.getParcelableArrayList<Bundle>("PLUGIN_CONFIG") as ArrayList<Bundle>
        val barcodeProps = pluginConfig.get(0).getBundle("PARAM_LIST")!!

        return DataWedgeConfig.fromBundle(barcodeProps)
    }

    /**
     * If the version is <= 6.5 we reduce the amount of configuration available.  There are
     * smarter ways to do this, e.g. DW 6.4 introduces profile creation (without profile
     * configuration) but to keep it simple, we just define a minimum of 6.5 for configuration
     * functionality
     */
    override suspend fun createProfile(
        profileName: String,
        packageName: String,
        profileIntentAction: String,
        profileIntentDelivery: IntentOutputIntentDelivery
    ) {
        //  Create and configure the DataWedge profile associated with this application
        //  For readability's sake, I have not defined each of the keys in the DWInterface file
        dwInterface.sendCommandString(
            DWInterface.DATAWEDGE_SEND_CREATE_PROFILE,
            profileName
        )

        configureProfile(profileName, packageName, profileIntentAction, profileIntentDelivery)
    }

    private fun configureProfile(
        profileName: String,
        packageName: String,
        profileIntentAction: String,
        profileIntentDelivery: IntentOutputIntentDelivery
    ) {
        // https://techdocs.zebra.com/datawedge/latest/guide/api/setconfig/
        val profileConfig = bundleOf(
            "PROFILE_NAME" to profileName,
            "PROFILE_ENABLED" to "true",
            "CONFIG_MODE" to "UPDATE",
            "APP_LIST" to arrayOf(
                bundleOf(
                    "PACKAGE_NAME" to packageName,
                    "ACTIVITY_LIST" to arrayOf("*")
                )
            )
        )

        val bundlePluginConfig = arrayListOf(
            IntentOutputConfiguration(
                enabled = true,
                intentAction = profileIntentAction,
                intentDelivery = profileIntentDelivery
            ).asBundle(),
            KeyboardOutputConfiguration(enabled = false).asBundle(),
            BarcodeInputConfiguration(enabled = true).asBundle()
        )

        profileConfig.putParcelableArrayList("PLUGIN_CONFIG", bundlePluginConfig)

        dwInterface.sendCommandBundle(DWInterface.DATAWEDGE_SEND_SET_CONFIG, profileConfig)
    }

    override suspend fun supportsConfigCreation(): Boolean {
        val versions = getVersions()
        val dataWedgeVersion = versions.dataWedgeVersion.toVersion(strict = false)
        return dataWedgeVersion >= "6.5.0".toVersion()
    }
}
