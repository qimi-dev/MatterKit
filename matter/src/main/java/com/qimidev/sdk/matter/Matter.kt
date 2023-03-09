package com.qimidev.sdk.matter

import android.app.Application
import chip.devicecontroller.ChipDeviceController
import chip.devicecontroller.ControllerParams
import chip.platform.*
import chip.setuppayload.SetupPayloadParser
import com.qimidev.sdk.matter.core.model.MatterSetupPayload

object Matter {

    private const val VENDOR_ID = 0xFFF4

    private lateinit var application: Application

    private lateinit var chipDeviceController: ChipDeviceController

    private lateinit var androidChipPlatform: AndroidChipPlatform

    internal fun initialize(application: Application) {
        this.application = application
        ChipDeviceController.loadJni()
        androidChipPlatform = AndroidChipPlatform(
            AndroidBleManager(),
            PreferencesKeyValueStoreManager(application),
            PreferencesConfigurationManager(application),
            NsdManagerServiceResolver(application),
            NsdManagerServiceBrowser(application),
            ChipMdnsCallbackImpl(),
            DiagnosticDataProviderImpl(application)
        )
        chipDeviceController = ChipDeviceController(
            ControllerParams.newBuilder().setControllerVendorId(VENDOR_ID).build()
        )
    }

    fun decodeSetupPayload(payload: String): Result<MatterSetupPayload> {
        return runCatching {
            SetupPayloadParser().parseQrCode(payload).run {
                MatterSetupPayload(
                    version = version,
                    vendorId = vendorId,
                    productId = productId,
                    discriminator = discriminator,
                    passcode = setupPinCode
                )
            }
        }
    }

}











