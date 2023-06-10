package com.qimidev.app.matterkit.core.matter

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import androidx.core.content.getSystemService
import chip.devicecontroller.ChipDeviceController
import chip.devicecontroller.ControllerParams
import chip.platform.AndroidBleManager
import chip.platform.AndroidChipPlatform
import chip.platform.ChipMdnsCallbackImpl
import chip.platform.DiagnosticDataProviderImpl
import chip.platform.NsdManagerServiceBrowser
import chip.platform.NsdManagerServiceResolver
import chip.platform.PreferencesConfigurationManager
import chip.platform.PreferencesKeyValueStoreManager
import chip.setuppayload.DiscoveryCapability
import chip.setuppayload.SetupPayloadParser
import com.qimidev.app.matterkit.core.model.MatterDiscoveryCapability
import com.qimidev.app.matterkit.core.model.MatterSetupPayload
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID
import kotlin.random.Random

@SuppressLint("MissingPermission")
internal class MatterImpl(context: Context): Matter {

    private val androidChipPlatform: AndroidChipPlatform

    private val chipDeviceController: ChipDeviceController

    private val bluetoothManager: BluetoothManager = context.getSystemService()!!

    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter

    init {
        ChipDeviceController.loadJni()
        androidChipPlatform = AndroidChipPlatform(
            AndroidBleManager(),
            PreferencesKeyValueStoreManager(context),
            PreferencesConfigurationManager(context),
            NsdManagerServiceResolver(context),
            NsdManagerServiceBrowser(context),
            ChipMdnsCallbackImpl(),
            DiagnosticDataProviderImpl(context)
        )
        chipDeviceController = ChipDeviceController(
            ControllerParams.newBuilder().setControllerVendorId(VENDOR_ID).build()
        )
    }

    override fun parseSetupPayload(payloadContent: String): Result<MatterSetupPayload> {
        return runCatching {
            SetupPayloadParser().parseQrCode(payloadContent).let {
                MatterSetupPayload(
                    version = it.version,
                    vendorId = it.vendorId,
                    productId = it.productId,
                    commissioningFlow = it.commissioningFlow,
                    discoveryCapabilities = it.discoveryCapabilities.map {
                        when (it) {
                            DiscoveryCapability.SOFT_AP -> MatterDiscoveryCapability.SOFT_AP
                            DiscoveryCapability.BLE -> MatterDiscoveryCapability.BLE
                            DiscoveryCapability.ON_NETWORK -> MatterDiscoveryCapability.ON_NETWORK
                        }
                    }.toSet(),
                    discriminator = it.discriminator,
                    hasShortDiscriminator = it.hasShortDiscriminator,
                    setupPinCode = it.setupPinCode
                )
            }
        }
    }

    override fun parseManualSetupPayload(payloadContent: String): Result<MatterSetupPayload> {
        return runCatching {
            SetupPayloadParser().parseManualEntryCode(payloadContent).let {
                MatterSetupPayload(
                    version = it.version,
                    vendorId = it.vendorId,
                    productId = it.productId,
                    commissioningFlow = it.commissioningFlow,
                    discoveryCapabilities = it.discoveryCapabilities.map {
                        when (it) {
                            DiscoveryCapability.SOFT_AP -> MatterDiscoveryCapability.SOFT_AP
                            DiscoveryCapability.BLE -> MatterDiscoveryCapability.BLE
                            DiscoveryCapability.ON_NETWORK -> MatterDiscoveryCapability.ON_NETWORK
                        }
                    }.toSet(),
                    discriminator = it.discriminator,
                    hasShortDiscriminator = it.hasShortDiscriminator,
                    setupPinCode = it.setupPinCode
                )
            }
        }
    }

    /**
     * Pair devices via Bluetooth protocol
     * @param discriminator discriminator of device
     * @param setupPinCode setupPinCode of device
     * @return The result of pairing
     */
    suspend fun pairDeviceByBle(discriminator: Int, setupPinCode: Long): Result<Unit> {
        // Step1 - Search for device via Bluetooth broadcast
        val bluetoothObjOfDevice: BluetoothDevice = withTimeoutOrNull(
            DEFAULT_TIMEOUT_FOR_SCANNING_BLUETOOTH_DEVICE
        ) {
            callbackFlow<BluetoothDevice> {
                val scanCallback: ScanCallback = object : ScanCallback() {
                    override fun onScanResult(callbackType: Int, result: ScanResult) {
                        trySend(result.device)
                    }
                }
                val serviceDataForFilter: ByteArray = getServiceData(discriminator)
                val scanFilter: ScanFilter = ScanFilter.Builder()
                    .setServiceData(ParcelUuid(UUID.fromString(CHIP_UUID)), serviceDataForFilter)
                    .build()
                val scanSettings: ScanSettings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build()
                val bluetoothLeScanner: BluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
                bluetoothLeScanner.startScan(listOf(scanFilter), scanSettings, scanCallback)
                awaitClose {
                    bluetoothLeScanner.stopScan(scanCallback)
                }
            }.first()
        } ?: return Result.failure(Exception("An exception is not implemented"))
        return TODO()
    }

    /**
     * Generate a random long integer for the device id
     * @return The device id.
     */
    private fun generateDeviceId(): Long = Random.nextLong()

    /**
     * Obtain the broadcast service data corresponding to the device
     * @param discriminator discriminator of device
     * @return The broadcast service data
     */
    private fun getServiceData(discriminator: Int): ByteArray {
        val opcode = 0
        val version = 0
        val versionDiscriminator = ((version and 0xf) shl 12) or (discriminator and 0xfff)
        return intArrayOf(opcode, versionDiscriminator, versionDiscriminator shr 8)
            .map { it.toByte() }
            .toByteArray()
    }

    companion object {

        private const val CHIP_UUID = "0000FFF6-0000-1000-8000-00805F9B34FB"

        private const val VENDOR_ID = 0xFFF4

        private const val DEFAULT_TIMEOUT_FOR_SCANNING_BLUETOOTH_DEVICE: Long = 10_000

    }

}