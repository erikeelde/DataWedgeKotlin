package com.darryncampbell.datawedgekotlin.configuration

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.datawedgerepository.DWInterface
import com.darryncampbell.datawedgekotlin.ScansPersister
import com.example.datawedgerepository.DataWedgeRepository
import com.example.datawedgerepository.DataWedgeScanner
import com.example.datawedgerepository.DataWedgeVersion
import com.darryncampbell.datawedgekotlin.getInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ConfigurationViewState {
    data object Loading : ConfigurationViewState
    data class LowVersion(val dataWedgeVersion: DataWedgeVersion) : ConfigurationViewState

    data class Loaded(
        val currentProfileName: String,
        val scanners: List<DataWedgeScanner>,
        val ean8Enabled: Boolean,
        val ean13Enabled: Boolean,
        val code39Enabled: Boolean,
        val code128Enabled: Boolean,
        val illuminationEnabled: Boolean,
        val picklistModeEnabled: Boolean,
    ) : ConfigurationViewState
}

class ConfigurationViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val dwInterface = DWInterface(applicationContext = application.applicationContext)
    private val scansPersister = ScansPersister(applicationContext = application.applicationContext)
    private val dataWedgeRepository = DataWedgeRepository.getInstance()

    internal val state: MutableStateFlow<ConfigurationViewState> =
        MutableStateFlow(ConfigurationViewState.Loading)

    init {
        viewModelScope.launch {
            val versions = dataWedgeRepository.getVersions()
            val dataWedgeVersion = versions.dataWedgeVersion
            if (dataWedgeVersion >= "6.5") {
                val profile = dataWedgeRepository.getActiveProfile()
                val configuration = dataWedgeRepository.getConfiguration(profile.name)
                val scanners = dataWedgeRepository.getScanners()
                delay(2000)
                state.update {
                    ConfigurationViewState.Loaded(
                        currentProfileName = profile.name,
                        scanners = scanners.filter { it.scannerConnectionState },
                        ean8Enabled = configuration.ean8,
                        ean13Enabled = configuration.ean13,
                        code39Enabled = configuration.code39,
                        code128Enabled = configuration.code128,
                        illuminationEnabled = configuration.illuminationMode,
                        picklistModeEnabled = configuration.pickListMode,
                    )
                }
            } else {
                state.update {
                    ConfigurationViewState.LowVersion(versions)
                }
            }
        }
    }

    fun ean8(enabled: Boolean) {
        state.update {
            if (it is ConfigurationViewState.Loaded) {
                it.copy(ean8Enabled = enabled)
            } else {
                it
            }
        }
        updateDecoder()
    }

    fun ean13(enabled: Boolean) {
        state.update {
            if (it is ConfigurationViewState.Loaded) {
                it.copy(ean13Enabled = enabled)
            } else {
                it
            }
        }
        updateDecoder()
    }

    fun code39(enabled: Boolean) {
        state.update {
            if (it is ConfigurationViewState.Loaded) {
                it.copy(code39Enabled = enabled)
            } else {
                it
            }
        }
        updateDecoder()
    }

    fun code128(enabled: Boolean) {
        state.update {
            if (it is ConfigurationViewState.Loaded) {
                it.copy(code128Enabled = enabled)
            } else {
                it
            }
        }
        updateDecoder()
    }

    fun illumination(enabled: Boolean) {
        state.update {
            if (it is ConfigurationViewState.Loaded) {
                it.copy(illuminationEnabled = enabled)
            } else {
                it
            }
        }
        updateDecoder()
    }

    fun picklist(enabled: Boolean) {
        state.update {
            if (it is ConfigurationViewState.Loaded) {
                it.copy(picklistModeEnabled = enabled)
            } else {
                it
            }
        }
        updateDecoder()
    }

    fun updateDecoder() {
        state.value.let { state ->
            if (state is ConfigurationViewState.Loaded) {
                setConfigForDecoder(
                    state.currentProfileName,
                    state.ean8Enabled,
                    state.ean13Enabled,
                    state.code39Enabled,
                    state.code128Enabled,
                    state.illuminationEnabled,
                    state.picklistModeEnabled
                )
            }
        }
    }

    suspend fun clearHistory() {
        scansPersister.clear()
    }

    fun setConfigForDecoder(
        profileName: String,
        ean8Value: Boolean,
        ean13Value: Boolean,
        code39Value: Boolean,
        code128Value: Boolean,
        torchEnabled: Boolean,
        picklistEnabled: Boolean
    ) {
        val torchValue = if (torchEnabled) "torch" else "off"
        val picklistValue = if (picklistEnabled) "2" else "0"
        dwInterface.setConfigForDecoder(
            profileName = profileName,
            ean8Value = ean8Value,
            ean13Value = ean13Value,
            code39Value = code39Value,
            code128Value = code128Value,
            illuminationValue = torchValue,
            picklistModeValue = picklistValue
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val savedStateHandle = extras.createSavedStateHandle()

                return ConfigurationViewModel(
                    application,
                    savedStateHandle
                ) as T
            }
        }
    }
}