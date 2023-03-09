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

    private val _addDeviceDialogUiState: MutableStateFlow<AddDeviceDialogUiState> =
        MutableStateFlow(AddDeviceDialogUiState.Idle)

    val addDeviceDialogUiState: StateFlow<AddDeviceDialogUiState> =
        _addDeviceDialogUiState.asStateFlow()

    fun startAddDevice() {
        _addDeviceDialogUiState.update {
            if (it is AddDeviceDialogUiState.Idle) AddDeviceDialogUiState.Searching else it
        }
    }

    fun handleSetupPayload(payload: String) {
        Timber.d("handleSetupPayload - payload=${payload}")
        _addDeviceDialogUiState.update {
            val payloadResult: Result<MatterSetupPayload> = Matter.decodeSetupPayload(payload)
            if (it is AddDeviceDialogUiState.Searching && payloadResult.isSuccess) {
                AddDeviceDialogUiState.Confirm(
                    payload = payloadResult.getOrThrow()
                )
            } else {
                it
            }
        }
    }

    fun confirmConnectionToDevice(payload: MatterSetupPayload) {
        Timber.d("confirmConnectionToDevice - payload=${payload}")
        _addDeviceDialogUiState.update {
            if (it is AddDeviceDialogUiState.Confirm) {
                AddDeviceDialogUiState.ProvideNetworkCredentials(
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
        _addDeviceDialogUiState.update {
            if (it is AddDeviceDialogUiState.ProvideNetworkCredentials) {
                AddDeviceDialogUiState.Connecting
            } else {
                it
            }
        }
        viewModelScope.launch {
            delay(2000)
            _addDeviceDialogUiState.value = AddDeviceDialogUiState.Success
        }
    }

    fun stopAddDevice() {
        _addDeviceDialogUiState.update {
            AddDeviceDialogUiState.Idle
        }
    }

}

sealed interface AddDeviceDialogUiState {

    object Idle : AddDeviceDialogUiState

    object Searching : AddDeviceDialogUiState

    data class Confirm(
        val payload: MatterSetupPayload
    ) : AddDeviceDialogUiState

    data class ProvideNetworkCredentials(
        val payload: MatterSetupPayload
    ) : AddDeviceDialogUiState

    object Connecting : AddDeviceDialogUiState

    object Success : AddDeviceDialogUiState

    object Failure : AddDeviceDialogUiState

}

data class MainUiState(
    val isAddingDevice: Boolean
)
















