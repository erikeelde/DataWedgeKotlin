package com.darryncampbell.datawedgekotlin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.darryncampbell.datawedgekotlin.scan.ScanViewModel
import com.example.datawedgerepository.DWInterface
import com.example.datawedgerepository.DataWedgeScan
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val scanViewModel: ScanViewModel by viewModels { ScanViewModel.Factory }
    private lateinit var dwInterface: DWInterface

    companion object {
        const val PROFILE_NAME = "DataWedgeKotlinDemo"
        const val PROFILE_INTENT_ACTION = "com.darryncampbell.datawedgekotlin.SCAN"
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

        if (DataWedgeHolder.get().isScanIntent(intent)) {
            lifecycleScope.launch {
                DataWedgeHolder.get().addScan(DataWedgeScan.fromIntent(intent))
            }
        }
    }
}
