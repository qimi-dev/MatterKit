package com.qimidev.sdk.matter.core.model

data class BaseSetupPayload(
    val discriminator: Int,
    val setupPinCode: Long
)