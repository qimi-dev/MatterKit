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
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.qimidev.app.matterkit.core.ui.component.MatterKitBottomDialog
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun rememberSetupDeviceState(): SetupDeviceState {
    return remember { SetupDeviceState() }
}

enum class SetupDeviceStep {

    GET_DEVICE_INFORMATION_BY_SCAN,

    GET_DEVICE_INFORMATION_BY_MANUAL_INPUT

}

@Stable
class SetupDeviceState {

    var isStartSetupDevice: Boolean by mutableStateOf(false)
        private set

    var setupDeviceStep: SetupDeviceStep by mutableStateOf(SetupDeviceStep.GET_DEVICE_INFORMATION_BY_SCAN)
        private set

    fun startSetupDevice() {
        isStartSetupDevice = true
    }

    fun switchToManualInput() {
        if (setupDeviceStep == SetupDeviceStep.GET_DEVICE_INFORMATION_BY_SCAN) {
            setupDeviceStep = SetupDeviceStep.GET_DEVICE_INFORMATION_BY_MANUAL_INPUT
        }
    }

    fun switchToScan() {
        if (setupDeviceStep == SetupDeviceStep.GET_DEVICE_INFORMATION_BY_MANUAL_INPUT) {
            setupDeviceStep = SetupDeviceStep.GET_DEVICE_INFORMATION_BY_SCAN
        }
    }

    fun stopSetupDevice() {
        isStartSetupDevice = false
    }

}

@Composable
internal fun SetupDeviceDialog(state: SetupDeviceState) {
    if (state.isStartSetupDevice) {
        MatterKitBottomDialog(
            onDismissRequest = state::stopSetupDevice,
            dismissOnClickOutside = false
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .animateContentSize()
            ) {
                when (state.setupDeviceStep) {
                    SetupDeviceStep.GET_DEVICE_INFORMATION_BY_SCAN -> {
                        ScanCodeDialogContent(
                            state = state,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    SetupDeviceStep.GET_DEVICE_INFORMATION_BY_MANUAL_INPUT -> {
                        ManualInputCodeDialogContent(
                            state = state,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanCodeDialogContent(
    state: SetupDeviceState,
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
            style = MaterialTheme.typography.titleSmall
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .heightIn(max = 180.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            CodeScanningBox(
                onResult = {
                    // TODO
                },
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
            onClick = state::switchToManualInput,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.switch_to_manual_input))
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun CodeScanningBox(
    onResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview: Preview? by remember {
        mutableStateOf(null)
    }
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
                                if (currentTimestamp - lastAnalyzedTimeStamp >= 300) {
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
                                                        onResult(barcodeValue)
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
private fun ManualInputCodeDialogContent(
    state: SetupDeviceState,
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
            style = MaterialTheme.typography.titleSmall
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
                onClick = state::switchToScan,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.switch_to_scan))
            }
            Button(
                onClick = {},
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.next_step))
            }
        }
    }
}


















