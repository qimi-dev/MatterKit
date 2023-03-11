package com.qimidev.demo.matterkit.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qimidev.sdk.matter.Matter
import com.qimidev.sdk.matter.core.model.MatterSetupPayload
import com.qimidev.sdk.matter.core.model.WifiCredentials
import com.qimidev.sdk.matter.exception.MatterException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _setupDialogUiStateStream: MutableStateFlow<SetupDialogUiState> =
        MutableStateFlow(SetupDialogUiState.Idle)

    val setupDialogUiStateStream: StateFlow<SetupDialogUiState> =
        _setupDialogUiStateStream.asStateFlow()

    private var pairDeviceJob: Job? = null

    fun startAddDevice() {
        _setupDialogUiStateStream.update {
            if (it is SetupDialogUiState.Idle) SetupDialogUiState.Searching else it
        }
    }

    fun handleSetupPayload(payload: String) {
        _setupDialogUiStateStream.update {
            val payloadResult: Result<MatterSetupPayload> = Matter.decodeSetupPayload(payload)
            if (it is SetupDialogUiState.Searching && payloadResult.isSuccess) {
                SetupDialogUiState.Confirm(
                    payload = payloadResult.getOrThrow()
                )
            } else {
                it
            }
        }
    }

    fun confirmConnectionToDevice(payload: MatterSetupPayload) {
        _setupDialogUiStateStream.update {
            if (it is SetupDialogUiState.Confirm) {
                SetupDialogUiState.ProvideNetworkCredentials(
                    payload = payload
                )
            } else {
                it
            }
        }
    }

    fun startPairDevice(
        payload: MatterSetupPayload,
        wifiCredentials: WifiCredentials
    ) {
        _setupDialogUiStateStream.update {
            if (it is SetupDialogUiState.ProvideNetworkCredentials) {
                SetupDialogUiState.Connecting
            } else {
                it
            }
        }
        pairDeviceJob = viewModelScope.launch {
            val pairException: MatterException? = Matter.pairDeviceWithBle(
                discriminator = payload.discriminator,
                setupPinCode = payload.passcode,
                ssid = wifiCredentials.ssid,
                password = wifiCredentials.password
            )
            Timber.d("pairDeviceWithBle: $pairException")
            if (pairException == null) {
                _setupDialogUiStateStream.value = SetupDialogUiState.Success
            } else {
                _setupDialogUiStateStream.value = SetupDialogUiState.Failure
            }
        }
    }

    fun dismissSetupDialog() {
        _setupDialogUiStateStream.update {
            when (it) {
                is SetupDialogUiState.Connecting -> return
                else -> SetupDialogUiState.Idle
            }
        }
    }

}

sealed interface SetupDialogUiState {

    object Idle : SetupDialogUiState

    object Searching : SetupDialogUiState

    data class Confirm(
        val payload: MatterSetupPayload
    ) : SetupDialogUiState

    data class ProvideNetworkCredentials(
        val payload: MatterSetupPayload
    ) : SetupDialogUiState

    object Connecting : SetupDialogUiState

    object Success : SetupDialogUiState

    object Failure : SetupDialogUiState

}















