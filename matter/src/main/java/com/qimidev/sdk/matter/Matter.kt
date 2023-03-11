package com.qimidev.sdk.matter

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import androidx.room.Room
import chip.devicecontroller.ChipDeviceController
import chip.devicecontroller.ControllerParams
import chip.devicecontroller.NetworkCredentials
import chip.platform.*
import chip.setuppayload.SetupPayloadParser
import com.qimidev.sdk.matter.core.database.MatterDatabase
import com.qimidev.sdk.matter.core.database.dao.MatterDeviceDao
import com.qimidev.sdk.matter.core.database.model.MatterDeviceEntity
import com.qimidev.sdk.matter.core.model.BaseSetupPayload
import com.qimidev.sdk.matter.core.model.BluetoothSetupPayload
import com.qimidev.sdk.matter.exception.MatterException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.coroutines.resume

object Matter {

    private const val VENDOR_ID = 0xFFF4

    private const val CHIP_UUID = "0000FFF6-0000-1000-8000-00805F9B34FB"

    private lateinit var application: Application

    private lateinit var bluetoothManager: BluetoothManager

    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var matterDatabase: MatterDatabase

    private lateinit var matterDeviceDao: MatterDeviceDao

    private lateinit var chipDeviceController: ChipDeviceController

    private lateinit var androidChipPlatform: AndroidChipPlatform

    internal fun initialize(application: Application) {
        this.application = application
        bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        matterDatabase = Room.databaseBuilder(
            application, MatterDatabase::class.java, "matter-database").build()
        ChipDeviceController.loadJni()
        matterDeviceDao = matterDatabase.matterDeviceDao()
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

    fun parseSetupPayload(payloadContent: String): Result<BaseSetupPayload> {
        return runCatching {
            SetupPayloadParser().parseQrCode(payloadContent).run {
                BaseSetupPayload(
                    discriminator = discriminator,
                    setupPinCode = setupPinCode
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun discoverBluetoothDevice(
        baseSetupPayload: BaseSetupPayload
    ): Flow<BluetoothSetupPayload> {
        return callbackFlow {
            val scanCallback: ScanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    trySend(
                        BluetoothSetupPayload(
                            discriminator = baseSetupPayload.discriminator,
                            setupPinCode = baseSetupPayload.setupPinCode,
                            bluetoothDevice = result.device
                        )
                    )
                }
            }
            val serviceData: ByteArray = getServiceData(baseSetupPayload.discriminator)
            val scanFilter: ScanFilter = ScanFilter.Builder()
                .setServiceData(ParcelUuid(UUID.fromString(CHIP_UUID)), serviceData)
                .build()
            val scanSettings: ScanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            bluetoothAdapter.bluetoothLeScanner?.startScan(listOf(scanFilter), scanSettings, scanCallback)
            awaitClose {
                bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun pairDevice(
        setupPayload: BluetoothSetupPayload,
        wifiSSID: String,
        wifiPassword: String
    ): MatterException? {
        if (!bluetoothAdapter.isEnabled) {
            return MatterException.BLUETOOTH_NOT_TURNED_ON
        }
        val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter.bluetoothLeScanner
        if (bluetoothLeScanner == null) {
            return MatterException.NO_BLUETOOTH_SCANNER_FOUND
        }
        val (connectionId, bluetoothGatt) = withTimeoutOrNull(10_000) {
            suspendCancellableCoroutine<Pair<Int, BluetoothGatt>> { continuation ->
                var connectionId: Int = 0
                val bluetoothGattCallback: BluetoothGattCallback = getWrappedCallback {
                    if (continuation.isActive) {
                        continuation.resume(connectionId to it)
                    }
                }
                val bluetoothGatt: BluetoothGatt = setupPayload.bluetoothDevice.connectGatt(
                    application, false, bluetoothGattCallback
                )
                val bleManager: BleManager = androidChipPlatform.bleManager
                connectionId = bleManager.addConnection(bluetoothGatt)
                bleManager.setBleCallback(object : BleCallback {

                    override fun onCloseBleComplete(connId: Int) {
                        // TODO
                    }

                    override fun onNotifyChipConnectionClosed(connId: Int) {
                        bluetoothGatt.close()
                    }

                })
                continuation.invokeOnCancellation {
                    bluetoothGatt.disconnect()
                }
            }
        } ?: return MatterException.FAILED_TO_CONNECT_TO_DEVICE
        return withContext(NonCancellable) {
            callbackFlow<MatterException?> {
                val deviceId: Long = matterDeviceDao.insertMatterDevice(MatterDeviceEntity())
                chipDeviceController.setCompletionListener(
                    object : PairCompletionListener() {
                        override fun onCommissioningComplete(nodeId: Long, errorCode: Int) {
                            // TODO release bluetooth device
                            chipDeviceController.close()
                            launch(start = CoroutineStart.UNDISPATCHED) {
                                matterDeviceDao.updateMatterDevice(
                                    matterDevice = MatterDeviceEntity(
                                        deviceId = deviceId,
                                        isPaired = true
                                    )
                                )
                                if (errorCode == 0) {
                                    send(null)
                                } else {
                                    send(MatterException.PAIRING_DEVICE_FAILED)
                                }
                            }
                        }
                    }
                )
                val networkCredentials: NetworkCredentials = NetworkCredentials.forWiFi(
                    NetworkCredentials.WiFiCredentials(wifiSSID, wifiPassword)
                )
                chipDeviceController.pairDevice(
                    bluetoothGatt, connectionId, deviceId, setupPayload.setupPinCode, networkCredentials
                )
                awaitClose()
            }.first()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getWrappedCallback(onCompleted: (BluetoothGatt) -> Unit): BluetoothGattCallback {
        return object : BluetoothGattCallback() {

            private val wrappedCallback: BluetoothGattCallback =
                androidChipPlatform.bleManager.callback

            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                wrappedCallback.onConnectionStateChange(gatt, status, newState)
                if (newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                    gatt.discoverServices()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                wrappedCallback.onServicesDiscovered(gatt, status)
                gatt.requestMtu(247)
            }

            override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
                super.onMtuChanged(gatt, mtu, status)
                wrappedCallback.onMtuChanged(gatt, mtu, status)
                onCompleted(gatt)
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                wrappedCallback.onCharacteristicChanged(gatt, characteristic)
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                wrappedCallback.onCharacteristicRead(gatt, characteristic, status)
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                super.onCharacteristicWrite(gatt, characteristic, status)
                wrappedCallback.onCharacteristicWrite(gatt, characteristic, status)
            }

            override fun onDescriptorRead(
                gatt: BluetoothGatt,
                descriptor: BluetoothGattDescriptor,
                status: Int
            ) {
                super.onDescriptorRead(gatt, descriptor, status)
                wrappedCallback.onDescriptorRead(gatt, descriptor, status)
            }

            override fun onDescriptorWrite(
                gatt: BluetoothGatt,
                descriptor: BluetoothGattDescriptor,
                status: Int
            ) {
                super.onDescriptorWrite(gatt, descriptor, status)
                wrappedCallback.onDescriptorWrite(gatt, descriptor, status)
            }

            override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
                super.onReadRemoteRssi(gatt, rssi, status)
                wrappedCallback.onReadRemoteRssi(gatt, rssi, status)
            }

            override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
                super.onReliableWriteCompleted(gatt, status)
                wrappedCallback.onReliableWriteCompleted(gatt, status)
            }

        }
    }

    private fun getServiceData(discriminator: Int): ByteArray {
        val opcode = 0
        val version = 0
        val versionDiscriminator = ((version and 0xf) shl 12) or (discriminator and 0xfff)
        return intArrayOf(opcode, versionDiscriminator, versionDiscriminator shr 8)
            .map { it.toByte() }
            .toByteArray()
    }

}











