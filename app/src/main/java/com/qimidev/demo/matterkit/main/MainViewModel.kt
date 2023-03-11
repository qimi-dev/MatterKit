package com.qimidev.demo.matterkit.main

import android.net.wifi.WifiSsid
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qimidev.sdk.matter.Matter
import com.qimidev.sdk.matter.core.model.MatterSetupPayload
import com.qimidev.sdk.matter.exception.MatterException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _setupDeviceUiStateStream: MutableStateFlow<SetupDeviceUiState> =
        MutableStateFlow(SetupDeviceUiState.Closed)

    val setupDeviceUiStateStream: StateFlow<SetupDeviceUiState> =
        _setupDeviceUiStateStream.asStateFlow()

    fun startSetupDevice() {
        _setupDeviceUiStateStream.update {
            if (it is SetupDeviceUiState.Closed) SetupDeviceUiState.Scanning else it
        }
    }

    fun decodeSetupPayload(payloadContent: String) {
        Matter.decodeSetupPayload(payloadContent)
            .onSuccess {
                _setupDeviceUiStateStream.compareAndSet(
                    expect = SetupDeviceUiState.Scanning,
                    update = SetupDeviceUiState.Ask(payload = it)
                )
            }
    }

    fun confirmSetupDevice(payload: MatterSetupPayload) {
        _setupDeviceUiStateStream.update {
            if (it is SetupDeviceUiState.Ask) {
                SetupDeviceUiState.ProvideNetworkCredentials(
                    payload = payload
                )
            } else {
                it
            }
        }
    }

    fun provideNetworkCredentials(
        payload: MatterSetupPayload,
        wifiSSID: String,
        wifiPassword: String
    ) {
        _setupDeviceUiStateStream.update {
            if (it is SetupDeviceUiState.ProvideNetworkCredentials) {
                SetupDeviceUiState.Pairing
            } else {
                return
            }
        }
        startPairDevice(payload, wifiSSID, wifiPassword)
    }

    private fun startPairDevice(
        payload: MatterSetupPayload,
        wifiSSID: String,
        wifiPassword: String
    ) {
        viewModelScope.launch {
            val pairException: MatterException? = Matter.pairDeviceWithBle(
                discriminator = payload.discriminator,
                setupPinCode = payload.passcode,
                ssid = wifiSSID,
                password = wifiPassword
            )
            if (pairException == null) {
                _setupDeviceUiStateStream.value = SetupDeviceUiState.Success
            } else {
                _setupDeviceUiStateStream.value = SetupDeviceUiState.Failure
            }
        }
    }

    fun stopSetupDevice() {
        _setupDeviceUiStateStream.update {
            when (it) {
                is SetupDeviceUiState.Pairing -> return
                else -> SetupDeviceUiState.Closed
            }
        }
    }

}

sealed interface SetupDeviceUiState {

    object Closed : SetupDeviceUiState

    object Scanning : SetupDeviceUiState

    data class Ask(
        val payload: MatterSetupPayload
    ) : SetupDeviceUiState

    data class ProvideNetworkCredentials(
        val payload: MatterSetupPayload
    ) : SetupDeviceUiState

    object Pairing : SetupDeviceUiState

    object Success : SetupDeviceUiState

    object Failure : SetupDeviceUiState

}















