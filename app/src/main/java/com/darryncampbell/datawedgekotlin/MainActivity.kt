package com.darryncampbell.datawedgekotlin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.darryncampbell.datawedgekotlin.scan.ScanViewModel
import com.example.datawedgerepository.DWInterface
import com.example.datawedgerepository.DataWedgeRepository
import com.example.datawedgerepository.DataWedgeScan
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val scanViewModel: ScanViewModel by viewModels { ScanViewModel.Factory }
    private lateinit var dwInterface: DWInterface

    companion object {
        const val PROFILE_NAME = "DataWedgeKotlinDemo"
        const val PROFILE_INTENT_ACTION = "com.darryncampbell.datawedgekotlin.SCAN"

        // Use "0" for Start Activity, "1" for Start Service, "2" for Broadcast
        const val PROFILE_INTENT_START_ACTIVITY = "0"
        const val PROFILE_INTENT_START_BROADCAST = "2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dwInterface = DWInterface(applicationContext = application.applicationContext)

        setContent {
            ApplicationView(scanViewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val dataWedgeRepository = DataWedgeRepository.getInstance()

        if (dataWedgeRepository.isScanIntent(intent)) {
            lifecycleScope.launch {
                dataWedgeRepository.addScan(DataWedgeScan.fromIntent(intent))
            }
        }
    }
}
