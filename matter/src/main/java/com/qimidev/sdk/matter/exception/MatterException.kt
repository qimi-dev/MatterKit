package com.qimidev.sdk.matter.exception

class MatterException private constructor(message: String) : Exception(message) {

    companion object {

        val BLUETOOTH_NOT_TURNED_ON = MatterException("Bluetooth not turned on")

        val NO_BLUETOOTH_SCANNER_FOUND = MatterException("No bluetooth scanner found")

        val DEVICE_NOT_FOUND = MatterException("Device not found")

        val FAILED_TO_CONNECT_TO_DEVICE = MatterException("Failed to connect to device")

        val PAIRING_DEVICE_FAILED = MatterException("Pairing device failed")

    }

}