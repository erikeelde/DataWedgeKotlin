package com.darryncampbell.datawedgekotlin

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darryncampbell.datawedgekotlin.configuration.ConfigurationView
import com.darryncampbell.datawedgekotlin.configuration.ConfigurationViewModel
import com.darryncampbell.datawedgekotlin.scan.ScanView
import com.darryncampbell.datawedgekotlin.scan.ScanViewModel

@Composable
fun ApplicationView(
    scanViewModel: ScanViewModel,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "scan") {
        composable("scan") {
            ScanView(scanViewModel) { navController.navigate("configuration") }
        }
        composable("configuration") {
            ConfigurationView(
                configurationViewModel = viewModel(factory = ConfigurationViewModel.Factory),
                navigateToScan = { navController.navigate("scan") })
        }
    }
}
