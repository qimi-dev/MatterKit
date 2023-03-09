package com.qimidev.sdk.matter.core.model

import chip.devicecontroller.NetworkCredentials.WiFiCredentials

data class WifiCredentials(
    val ssid: String,
    val password: String
)
