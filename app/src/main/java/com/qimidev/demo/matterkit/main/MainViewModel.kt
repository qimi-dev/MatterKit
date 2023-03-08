package com.qimidev.demo.matterkit.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _addDeviceDialogUiState: MutableStateFlow<AddDeviceDialogUiState> =
        MutableStateFlow(AddDeviceDialogUiState.Connecting)

    val addDeviceDialogUiState: StateFlow<AddDeviceDialogUiState> =
        _addDeviceDialogUiState.asStateFlow()

    fun startAddDevice() {
        _addDeviceDialogUiState.update {
            if (it is AddDeviceDialogUiState.Idle) AddDeviceDialogUiState.Searching else it
        }
    }

    fun parseQrCodeContent(qrCodeContent: String) {

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

    object Confirm : AddDeviceDialogUiState

    object ProvideNetworkCredentials : AddDeviceDialogUiState

    object Connecting : AddDeviceDialogUiState

}

data class MainUiState(
    val isAddingDevice: Boolean
)
















