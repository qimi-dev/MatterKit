package com.qimidev.sdk.matter.core.model

import android.bluetooth.BluetoothDevice

data class BluetoothSetupPayload(
    val discriminator: Int,
    val setupPinCode: Long,
    val bluetoothDevice: BluetoothDevice
)
