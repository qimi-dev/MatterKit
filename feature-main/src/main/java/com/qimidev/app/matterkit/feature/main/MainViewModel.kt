package com.qimidev.app.matterkit.feature.main

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qimidev.app.matterkit.core.matter.Matter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val matter: Matter
): ViewModel() {

    private val resource: Resources = context.resources

    /**
     * Private device configuration dialog Ui state.
     */
    private val _setupDeviceDialogUiState: MutableStateFlow<SetupDeviceDialogUiState> =
        MutableStateFlow(SetupDeviceDialogUiState.Hidden)

    /**
     * Exposed device configuration dialog Ui state.
     */
    val setupDeviceDialogUiState: StateFlow<SetupDeviceDialogUiState> =
        _setupDeviceDialogUiState.asStateFlow()

    /**
     * Opens the device configuration dialog to add devices.
     */
    fun openSetupDeviceDialog() {
        _setupDeviceDialogUiState.update {
            SetupDeviceDialogUiState.ParseQrCode(
                onParseQrCode = ::onParseQrCode,
                onToggleToManualEntryCode = ::onToggleToManualEntryCode,
                onDismissRequest = ::onDismissSetupDeviceDialog
            )
        }
    }

    /**
     * Handles the request to close the device configuration dialog.
     */
    private fun onDismissSetupDeviceDialog() {
        _setupDeviceDialogUiState.updateAndGet {
            when (it) {
                is SetupDeviceDialogUiState.ParseQrCode,
                is SetupDeviceDialogUiState.ParseManualEntryCode,
                is SetupDeviceDialogUiState.ProvideWifiCredentials -> {
                    SetupDeviceDialogUiState.Hidden
                }
                else -> it
            }
        }.apply {
            if (this is SetupDeviceDialogUiState.Hidden) {
                // TODO Release relative resource
            }
        }
    }

    /**
     * Process the content of the scanned QR code.
     * @param qrCode The content of QR code.
     */
    private fun onParseQrCode(qrCode: String) {
        if (_setupDeviceDialogUiState.value !is SetupDeviceDialogUiState.ParseQrCode) {
            return
        }
        if (qrCode.startsWith("MT:")) {
            matter.parseSetupPayload(qrCode)
        } else {
            matter.parseManualSetupPayload(qrCode)
        }.onSuccess {
            _setupDeviceDialogUiState.value = SetupDeviceDialogUiState.ProvideWifiCredentials(
                onProvideWifiCredentials = ::onProvideWifiCredentials,
                onDismissRequest = ::onDismissSetupDeviceDialog
            )
        }
    }

    /**
     * Handle manually entered pairing codes. If the parsing is successful,
     * it will jump to the wifi credential providing page, otherwise an error
     * message will be displayed.
     * @param manualEntryCode The pairing codes.
     */
    private fun onParseManualEntryCode(manualEntryCode: String) {
        matter.parseManualSetupPayload(manualEntryCode)
            .onSuccess {
                _setupDeviceDialogUiState.value = SetupDeviceDialogUiState.ProvideWifiCredentials(
                    onProvideWifiCredentials = ::onProvideWifiCredentials,
                    onDismissRequest = ::onDismissSetupDeviceDialog
                )
            }.onFailure {
                _setupDeviceDialogUiState.update {
                    if (it is SetupDeviceDialogUiState.ParseManualEntryCode) {
                        it.copy(errorMessage = resource.getString(R.string.invalid_sharing_code))
                    } else {
                        it
                    }
                }
            }
    }

    /**
     * Switch to the manual input pairing code page.
     */
    private fun onToggleToManualEntryCode() {
        _setupDeviceDialogUiState.update {
            SetupDeviceDialogUiState.ParseManualEntryCode(
                errorMessage = null,
                onParseManualEntryCode = ::onParseManualEntryCode,
                onToggleToQrCode = ::onToggleToQrCode,
                onDismissRequest = ::onDismissSetupDeviceDialog
            )
        }
    }

    /**
     * Switch to scan pairing code page。
     */
    private fun onToggleToQrCode() {
        _setupDeviceDialogUiState.update {
            SetupDeviceDialogUiState.ParseQrCode(
                onParseQrCode = ::onParseQrCode,
                onToggleToManualEntryCode = ::onToggleToManualEntryCode,
                onDismissRequest = ::onDismissSetupDeviceDialog
            )
        }
    }

    private fun onProvideWifiCredentials(wifiSSID: String, wifiPassword: String) {
        _setupDeviceDialogUiState.update {
            // TODO Connecting to device
            viewModelScope.launch {
                delay(1000)
                _setupDeviceDialogUiState.update {
                    SetupDeviceDialogUiState.ProvideDeviceName(
                        onDismissRequest = ::onDismissSetupDeviceDialog
                    )
                }
            }

            SetupDeviceDialogUiState.Connecting(
                isClosable = true,
                onDismissRequest = ::onDismissSetupDeviceDialog
            )
        }
    }

}

sealed interface SetupDeviceDialogUiState {

    object Hidden : SetupDeviceDialogUiState

    data class ParseQrCode(
        val onParseQrCode: (String) -> Unit,
        val onToggleToManualEntryCode: () -> Unit,
        val onDismissRequest: () -> Unit
    ) : SetupDeviceDialogUiState

    data class ParseManualEntryCode(
        val errorMessage: String?,
        val onParseManualEntryCode: (String) -> Unit,
        val onToggleToQrCode: () -> Unit,
        val onDismissRequest: () -> Unit
    ) : SetupDeviceDialogUiState

    data class ProvideWifiCredentials(
        val onProvideWifiCredentials: (String, String) -> Unit,
        val onDismissRequest: () -> Unit
    ) : SetupDeviceDialogUiState

    data class Connecting(
        val isClosable: Boolean,
        val onDismissRequest: () -> Unit
    ) : SetupDeviceDialogUiState

    data class ProvideDeviceName(
        val onDismissRequest: () -> Unit
    ) : SetupDeviceDialogUiState

}
