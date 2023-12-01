package com.darryncampbell.datawedgekotlin.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.datawedgerepository.DataWedgeScanner
import com.example.datawedgerepository.DataWedgeVersion
import com.darryncampbell.datawedgekotlin.ui.SwitchWithLabel
import com.darryncampbell.datawedgekotlin.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

interface ConfigurationActions {
    fun ean8(enabled: Boolean)
    fun ean13(enabled: Boolean)
    fun code39(enabled: Boolean)
    fun code128(enabled: Boolean)
    fun clearHistory()
    fun illumination(enabled: Boolean)
    fun picklist(enabled: Boolean)
}

@Composable
fun ConfigurationView(
    configurationViewModel: ConfigurationViewModel,
    navigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    MyApplicationTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = { navigateToScan() },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null
                            )
                        })
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.AccountBox,
                                contentDescription = null
                            )
                        })
                }
            }
        ) { paddingValues ->
            val configurationViewState by configurationViewModel.state.collectAsStateWithLifecycle()

            val scope = rememberCoroutineScope()

            ConfigurationView(
                configurationViewState = configurationViewState,
                configurationActions = object : ConfigurationActions {
                    override fun ean8(enabled: Boolean) {
                        configurationViewModel.ean8(enabled)
                    }

                    override fun ean13(enabled: Boolean) {
                        configurationViewModel.ean13(enabled)
                    }

                    override fun code39(enabled: Boolean) {
                        configurationViewModel.code39(enabled)
                    }

                    override fun code128(enabled: Boolean) {
                        configurationViewModel.code128(enabled)
                    }

                    override fun clearHistory() {
                        scope.launch {
                            configurationViewModel.clearHistory()
                        }
                    }

                    override fun illumination(enabled: Boolean) {
                        configurationViewModel.illumination(enabled)
                    }

                    override fun picklist(enabled: Boolean) {
                        configurationViewModel.picklist(enabled)
                    }
                },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun ConfigurationView(
    configurationViewState: ConfigurationViewState,
    configurationActions: ConfigurationActions,
    modifier: Modifier = Modifier,
) {

    when (configurationViewState) {
        is ConfigurationViewState.Loaded ->
            ConfigurationViewLoaded(
                configurationViewState = configurationViewState,
                configurationActions = configurationActions,
                modifier = modifier,
            )

        is ConfigurationViewState.Loading ->
            ConfigurationViewLoading(
                modifier = modifier,
            )

        is ConfigurationViewState.LowVersion ->
            ConfigurationViewTooLowVersion(
                dataWedgeVersion = configurationViewState.dataWedgeVersion,
                modifier = modifier
            )
    }
}

@Composable
fun ConfigurationViewTooLowVersion(dataWedgeVersion: DataWedgeVersion, modifier: Modifier) {
    Text("Datawedge version ${dataWedgeVersion.dataWedgeVersion} is not configurable")
}

@Composable
fun ConfigurationViewLoading(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(modifier = modifier)
}


@Preview
@Composable
fun ConfigurationViewLoadedPreview() {
    MyApplicationTheme {
        Surface {
            ConfigurationViewLoaded(
                ConfigurationViewState.Loaded(
                    currentProfileName = "CURRENT_PROFILE",
                    scanners = listOf(
                        DataWedgeScanner(
                            scannerConnectionState = true,
                            scannerIdentifier = "Scanner1",
                            scannerName = "First scanner",
                            scannerIndex = 0
                        ),
                        DataWedgeScanner(
                            scannerConnectionState = true,
                            scannerIdentifier = "Scanner2",
                            scannerName = "Second scanner",
                            scannerIndex = 0
                        )
                    ),
                    ean8Enabled = true,
                    ean13Enabled = true,
                    code39Enabled = true,
                    code128Enabled = true,
                    illuminationEnabled = true,
                    picklistModeEnabled = true,
                ),
                configurationActions = object : ConfigurationActions {
                    override fun ean8(enabled: Boolean) = Unit
                    override fun ean13(enabled: Boolean) = Unit
                    override fun code39(enabled: Boolean) = Unit
                    override fun code128(enabled: Boolean) = Unit
                    override fun illumination(enabled: Boolean) = Unit
                    override fun picklist(enabled: Boolean) = Unit
                    override fun clearHistory() = Unit
                }
            )
        }
    }
}

@Composable
fun ConfigurationViewLoaded(
    configurationViewState: ConfigurationViewState.Loaded,
    configurationActions: ConfigurationActions,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(text = "Active Profile")
        Text(text = configurationViewState.currentProfileName)
        Row {

            SwitchWithLabel(
                checked = configurationViewState.ean8Enabled,
                onCheckedChange = { configurationActions.ean8(it) },
                labelContent = { Text("Ean 8") }
            )
            SwitchWithLabel(
                checked = configurationViewState.ean13Enabled,
                onCheckedChange = { configurationActions.ean13(it) },
                labelContent = { Text("Ean 13") }
            )
        }
        Row {
            SwitchWithLabel(
                checked = configurationViewState.code39Enabled,
                onCheckedChange = { configurationActions.code39(it) },
                labelContent = { Text("Code 39") }
            )
            SwitchWithLabel(
                checked = configurationViewState.code128Enabled,
                onCheckedChange = { configurationActions.code128(it) },
                labelContent = { Text("Code 128") }
            )
        }

        var expanded by rememberSaveable { mutableStateOf(false) }
        Text(text = "Available Scanners")
        Text(modifier = Modifier.clickable { expanded = true }, text = "Current item")
        DropdownMenu(expanded = expanded,
            onDismissRequest = { expanded = false }) {
            configurationViewState.scanners.forEach {
                DropdownMenuItem(
                    text = { Text(it.scannerName) },
                    onClick = { expanded = false }
                )
            }
        }
        SwitchWithLabel(
            checked = configurationViewState.illuminationEnabled,
            onCheckedChange = { configurationActions.illumination(it) },
            labelContent = { Text("Illumination") }
        )
        SwitchWithLabel(
            checked = configurationViewState.picklistModeEnabled,
            onCheckedChange = { configurationActions.picklist(it) },
            labelContent = { Text("Picklist mode") }
        )
        Button(onClick = { configurationActions.clearHistory() }) {
            Text("Clear history")
        }
    }
}
