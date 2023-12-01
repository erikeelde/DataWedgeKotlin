package com.darryncampbell.datawedgekotlin.scan

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.darryncampbell.datawedgekotlin.DataWedgeHolder
import com.darryncampbell.datawedgekotlin.MainActivity
import com.darryncampbell.datawedgekotlin.ScansPersister
import com.example.datawedgerepository.DWInterface
import com.example.datawedgerepository.DataWedgeScan
import com.example.datawedgerepository.IntentOutputIntentDelivery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ScanViewState {
    data object Loading : ScanViewState
    data class Loaded(val dataWedgeScans: List<DataWedgeScan>) : ScanViewState
}

class ScanViewModel(applicationContext: Context, savedStateHandle: SavedStateHandle) : ViewModel() {
    private val scansPersister = ScansPersister(applicationContext = applicationContext)

    private val dwInterface: DWInterface = DWInterface(applicationContext = applicationContext)

    private val dataWedgeRepository = DataWedgeHolder.get()

    internal val state: MutableStateFlow<ScanViewState> = MutableStateFlow(ScanViewState.Loading)

    init {
        viewModelScope.launch {
            if (dataWedgeRepository.supportsConfigCreation()) {
                dataWedgeRepository
                    .createProfile(
                        MainActivity.PROFILE_NAME,
                        applicationContext.packageName,
                        MainActivity.PROFILE_INTENT_ACTION,
                        IntentOutputIntentDelivery.Activity
                    )
            }
        }
        viewModelScope.launch {
            val readScans = scansPersister.readScans()
            state.update {
                when (it) {
                    is ScanViewState.Loaded -> ScanViewState.Loaded(dataWedgeScans = it.dataWedgeScans + readScans)
                    ScanViewState.Loading -> ScanViewState.Loaded(dataWedgeScans = readScans)
                }
            }
        }
        viewModelScope.launch {
            dataWedgeRepository.scans.collect { dataWedgeScan ->
                state.update {
                    when (it) {
                        is ScanViewState.Loaded -> ScanViewState.Loaded(
                            dataWedgeScans = it.dataWedgeScans + dataWedgeScan
                        )

                        ScanViewState.Loading -> ScanViewState.Loaded(
                            dataWedgeScans = listOf(dataWedgeScan)
                        )
                    }
                }
                persistScans()
            }
        }
    }

    suspend fun persistScans() {
        val currentState = state.value
        if (currentState is ScanViewState.Loaded) {
            scansPersister.writeScans(currentState.dataWedgeScans)
        }
    }

    fun startScan() {
        dwInterface.startScan()
    }

    fun stopScan() {
        dwInterface.stopScan()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val savedStateHandle = extras.createSavedStateHandle()

                return ScanViewModel(
                    application,
                    savedStateHandle
                ) as T
            }
        }
    }
}

