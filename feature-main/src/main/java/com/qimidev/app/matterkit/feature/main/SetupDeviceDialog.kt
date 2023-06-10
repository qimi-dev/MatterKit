package com.qimidev.app.matterkit.feature.main

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.qimidev.app.matterkit.core.ui.component.MatterKitBottomDialog
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
internal fun SetupDeviceDialog(uiState: SetupDeviceDialogUiState) {
    MatterKitBottomDialog(
        onDismissRequest = {
            when (uiState) {
                is SetupDeviceDialogUiState.ParseQrCode -> uiState.onDismissRequest()
                is SetupDeviceDialogUiState.ParseManualEntryCode -> uiState.onDismissRequest()
                is SetupDeviceDialogUiState.ProvideWifiCredentials -> uiState.onDismissRequest()
                is SetupDeviceDialogUiState.Connecting -> {
                    if (uiState.isClosable) uiState.onDismissRequest()
                }
                is SetupDeviceDialogUiState.ProvideDeviceName -> uiState.onDismissRequest()
                else -> Unit
            }
        },
        dismissOnClickOutside = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .animateContentSize()
        ) {
            when (uiState) {
                is SetupDeviceDialogUiState.ParseQrCode -> {
                    ParseQrCodeContent(
                        onParseQrCode = uiState.onParseQrCode,
                        onToggleToManualEntryCode = uiState.onToggleToManualEntryCode,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is SetupDeviceDialogUiState.ParseManualEntryCode -> {
                    ParseManualEntryCodeContent(
                        onParseManualEntryCode = uiState.onParseManualEntryCode,
                        onToggleToQrCode = uiState.onToggleToQrCode,
                        errorMessage = uiState.errorMessage,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is SetupDeviceDialogUiState.ProvideWifiCredentials -> {
                    ProvideWifiCredentialsContent(
                        onProvideWifiCredentials = uiState.onProvideWifiCredentials,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is SetupDeviceDialogUiState.Connecting -> {
                    ConnectingContent(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is SetupDeviceDialogUiState.ProvideDeviceName -> {
                    ProvideDeviceNameContent(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun ParseQrCodeContent(
    onParseQrCode: (String) -> Unit,
    onToggleToManualEntryCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.add_device_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(id = R.string.add_device_suggest),
            style = MaterialTheme.typography.titleSmall.copy(
                textAlign = TextAlign.Center
            )
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .heightIn(max = 180.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            CodeScanningBox(
                onContentCallback = onParseQrCode,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_qr_code_24),
                contentDescription = null
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.scan_setup_code_title),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = stringResource(id = R.string.scan_setup_code_description),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_signal_strength_24),
                contentDescription = null
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.hold_phone_near_device_title),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = stringResource(id = R.string.hold_phone_near_device_description),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
            }
        }
        Button(
            onClick = onToggleToManualEntryCode,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.switch_to_manual_input))
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun CodeScanningBox(
    onContentCallback: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner by remember {
        mutableStateOf(object : LifecycleOwner {

            val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

            override val lifecycle: Lifecycle = lifecycleRegistry

        })
    }
    DisposableEffect(Unit) {
        lifecycleOwner.lifecycleRegistry.currentState = Lifecycle.State.STARTED
        onDispose {
            lifecycleOwner.lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }
    }
    var preview: Preview? by remember { mutableStateOf(null) }
    AndroidView(
        factory = { AndroidViewContext ->
            PreviewView(AndroidViewContext).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = modifier,
        update = { previewView ->
            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
            val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
                ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, object : ImageAnalysis.Analyzer {

                            private var lastAnalyzedTimeStamp = 0L

                            override fun analyze(image: ImageProxy) {
                                val currentTimestamp = System.currentTimeMillis()
                                if (currentTimestamp - lastAnalyzedTimeStamp >= 100) {
                                    image.image?.let { imageToAnalyze ->
                                        val options = BarcodeScannerOptions.Builder()
                                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                                            .build()
                                        val barcodeScanner = BarcodeScanning.getClient(options)
                                        val imageToProcess = InputImage.fromMediaImage(imageToAnalyze, image.imageInfo.rotationDegrees)

                                        barcodeScanner.process(imageToProcess)
                                            .addOnSuccessListener { barcodes ->
                                                barcodes.forEach { barcode ->
                                                    barcode.rawValue?.let { barcodeValue ->
                                                        onContentCallback(barcodeValue)
                                                    }
                                                }
                                            }
                                            .addOnFailureListener { exception ->

                                            }
                                            .addOnCompleteListener {
                                                image.close()
                                            }
                                    }
                                    lastAnalyzedTimeStamp = currentTimestamp
                                } else {
                                    image.close()
                                }
                            }

                        })
                    }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

@Composable
private fun ParseManualEntryCodeContent(
    onParseManualEntryCode: (String) -> Unit,
    onToggleToQrCode: () -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    var deviceSharingCode: String by remember { mutableStateOf("") }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.add_device_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(id = R.string.manual_input_device_code_description),
            style = MaterialTheme.typography.titleSmall.copy(
                textAlign = TextAlign.Center
            )
        )
        TextField(
            value = deviceSharingCode,
            onValueChange = {
                deviceSharingCode = it
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            supportingText = {
                if (errorMessage != null) { Text(text = errorMessage) }
            },
            isError = errorMessage != null,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onBackground,
                selectionColors = TextSelectionColors(
                    handleColor = MaterialTheme.colorScheme.onBackground,
                    backgroundColor = MaterialTheme.colorScheme.secondary
                )
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onToggleToQrCode,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.switch_to_scan))
            }
            Button(
                onClick = { onParseManualEntryCode(deviceSharingCode) },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.next_step))
            }
        }
    }
}

@Composable
private fun ProvideWifiCredentialsContent(
    onProvideWifiCredentials: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var wifiSSID: String by remember { mutableStateOf("") }
    var wifiPassword: String by remember { mutableStateOf("") }
    var isPasswordVisible: Boolean by remember { mutableStateOf(false) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.provide_wifi_credentials_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(id = R.string.provide_wifi_credentials_hint),
            style = MaterialTheme.typography.titleSmall.copy(
                textAlign = TextAlign.Center
            )
        )
        OutlinedTextField(
            value = wifiSSID,
            onValueChange = {
                wifiSSID = it
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            label = {
                Text(text = stringResource(id = R.string.input_wifi_ssid_hint))
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onBackground,
                selectionColors = TextSelectionColors(
                    handleColor = MaterialTheme.colorScheme.onBackground,
                    backgroundColor = MaterialTheme.colorScheme.secondary
                ),
                focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
            )
        )
        OutlinedTextField(
            value = wifiPassword,
            onValueChange = {
                wifiPassword = it
            },
            label = {
                Text(text = stringResource(id = R.string.input_wifi_password_hint))
            },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                val iconPainter: Painter = if (isPasswordVisible) {
                    painterResource(id = R.drawable.ic_visibility_24)
                } else {
                    painterResource(id = R.drawable.ic_visibility_off_24)
                }
                IconButton(
                    onClick = {
                        isPasswordVisible = !isPasswordVisible
                    }
                ){
                    Icon(
                        painter = iconPainter,
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onBackground,
                selectionColors = TextSelectionColors(
                    handleColor = MaterialTheme.colorScheme.onBackground,
                    backgroundColor = MaterialTheme.colorScheme.secondary
                ),
                focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                focusedTrailingIconColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onBackground
            )
        )
        Button(
            onClick = { onProvideWifiCredentials(wifiSSID, wifiPassword) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.start_pair_device))
        }
    }
}

@Composable
private fun ConnectingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.connecting_to_device_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(id = R.string.connecting_to_device_hint),
            style = MaterialTheme.typography.titleSmall.copy(
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Surface(shape = CircleShape) {
            LinearProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProvideDeviceNameContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.provide_device_name_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(id = R.string.provide_device_name_hint),
            style = MaterialTheme.typography.titleSmall.copy(
                textAlign = TextAlign.Center
            )
        )

    }
}














