package com.qimidev.sdk.matter.core.model

data class MatterSetupPayload(
    val version: Int,
    val vendorId: Int,
    val productId: Int,
    val discriminator: Int,
    val passcode: Long
)
