package com.qimidev.app.matterkit.core.model

data class MatterSetupPayload(
    val version: Int,
    val vendorId: Int,
    val productId: Int,
    val commissioningFlow: Int,
    val discoveryCapabilities: Set<MatterDiscoveryCapability>,
    val discriminator: Int,
    val hasShortDiscriminator: Boolean,
    val setupPinCode: Long
)
