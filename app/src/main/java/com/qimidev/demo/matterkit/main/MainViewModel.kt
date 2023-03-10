package com.qimidev.demo.matterkit.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qimidev.sdk.matter.Matter
import com.qimidev.sdk.matter.core.model.MatterSetupPayload
import com.qimidev.sdk.matter.core.model.WifiCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun startAddDevice() {
        _setupDialogUiStateStream.update {
            if (it is SetupDialogUiState.Idle) SetupDialogUiState.Searching else it
        }
    }

    fun handleSetupPayload(payload: String) {
        Timber.d("handleSetupPayload - payload=${payload}")
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
        Timber.d("confirmConnectionToDevice - payload=${payload}")
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

    fun startConnectionToDevice(
        payload: MatterSetupPayload,
        wifiCredentials: WifiCredentials
    ) {
        Timber.d("startConnectionToDevice - payload=${payload}, wifiCredentials=${wifiCredentials}")
        _setupDialogUiStateStream.update {
            if (it is SetupDialogUiState.ProvideNetworkCredentials) {
                SetupDialogUiState.Connecting
            } else {
                it
            }
        }
        viewModelScope.launch {
            delay(2000)
            _setupDialogUiStateStream.value = SetupDialogUiState.Success
        }
    }

    fun stopAddDevice() {
        _setupDialogUiStateStream.update {
            SetupDialogUiState.Idle
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















