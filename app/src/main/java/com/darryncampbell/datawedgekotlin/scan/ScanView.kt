package com.darryncampbell.datawedgekotlin.scan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darryncampbell.datawedgekotlin.ui.theme.MyApplicationTheme
import com.example.datawedgerepository.DataWedgeScan
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScanView(scanViewModel: ScanViewModel, navigateToConfiguration: () -> Unit) {
    MyApplicationTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = { /*TODO*/ },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navigateToConfiguration() },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.AccountBox,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        ) { paddingValues ->
            val scanViewState by scanViewModel.state.collectAsStateWithLifecycle()
            ScanView(
                mainViewState = scanViewState,
                startScan = { scanViewModel.startScan() },
                stopScan = { scanViewModel.stopScan() },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun ScanView(
    mainViewState: ScanViewState,
    startScan: () -> Unit,
    stopScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val state = mainViewState) {
        is ScanViewState.Loaded -> ScanViewLoaded(
            dataWedgeScans = state.dataWedgeScans,
            startScan = startScan,
            stopScan = stopScan,
            modifier = modifier
        )

        ScanViewState.Loading -> ScanViewLoading(modifier = modifier)
    }
}

@Composable
fun ScanViewLoading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
fun ScanViewLoaded(
    dataWedgeScans: List<DataWedgeScan>,
    startScan: () -> Unit,
    stopScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isScanning by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        LazyColumn {
            dataWedgeScans.forEach {
                item {
                    ListItem(
                        headlineContent = { Text(it.data) },
                        supportingContent = { Text(text = it.symbology) },
                        trailingContent = { Text(text = instantToString(it.scannedInstant)) }
                    )
                }
            }
        }
        Button(onClick = {
            if (isScanning) {
                isScanning = false
                stopScan()
            } else {
                isScanning = true
                startScan()
            }
        }) {
            if (isScanning) {
                Text(text = "STOP")
            } else {
                Text(text = "SCAN")
            }
        }
    }
}

fun instantToString(scannedInstant: Instant): String {
    val localDateTime = scannedInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    return buildString {
        // dd/MM/yyyy HH:mm:ss
        append(localDateTime.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
        append(" ")
        append(localDateTime.dayOfMonth)
        append(" ")
        append(localDateTime.hour)
        append(":")
        append(localDateTime.minute)
        append(":")
        append(localDateTime.second)
    }
}
