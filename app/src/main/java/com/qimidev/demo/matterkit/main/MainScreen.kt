package com.qimidev.demo.matterkit.main

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.qimidev.demo.matterkit.R
import com.qimidev.demo.matterkit.ui.DialogScaffold
import com.qimidev.sdk.matter.core.model.MatterSetupPayload
import com.qimidev.sdk.matter.core.model.WifiCredentials
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val addDeviceDialogUiState: AddDeviceDialogUiState by viewModel
        .addDeviceDialogUiState.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            MainTopBar(
                onAddDevice = viewModel::startAddDevice
            )
        }
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            MainScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
        AddDeviceDialog(
            uiState = addDeviceDialogUiState,
            onStopAddDevice = viewModel::stopAddDevice,
            onHandleSetupPayload = viewModel::handleSetupPayload,
            onConfirmConnectionToDevice = viewModel::confirmConnectionToDevice,
            onProvideNetworkCredentials = viewModel::startConnectionToDevice
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    onAddDevice: () -> Unit,
    modifier: Modifier = Modifier
) {
    MediumTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.title_main_page),
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        modifier = modifier,
        actions = {
            IconButton(
                onClick = onAddDevice
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun MainScreen(
    modifier: Modifier = Modifier
) {

}

@Composable
private fun AddDeviceDialog(
    uiState: AddDeviceDialogUiState,
    onStopAddDevice: () -> Unit,
    onHandleSetupPayload: (String) -> Unit,
    onConfirmConnectionToDevice: (MatterSetupPayload) -> Unit,
    onProvideNetworkCredentials: (MatterSetupPayload, WifiCredentials) -> Unit
) {
    when (uiState) {
        is AddDeviceDialogUiState.Searching -> {
            DialogScaffold(
                onDismissRequest = onStopAddDevice,
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.title_add_device),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.hint_add_device),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Surface(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .heightIn(max = 180.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        CodeScanningBox(
                            onResult = onHandleSetupPayload,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                text = stringResource(id = R.string.title_scan_qr_code),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = stringResource(id = R.string.content_scan_qr_code),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                text = stringResource(id = R.string.title_hold_phone_near_device),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = stringResource(id = R.string.content_hold_phone_near_device),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray
                                )
                            )
                        }
                    }
                }
            }
        }
        is AddDeviceDialogUiState.Confirm -> {
            DialogScaffold(
                onDismissRequest = onStopAddDevice,
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.title_confirm_add_device),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.lottie_home)
                    )
                    LottieAnimation(
                        composition = composition,
                        modifier = Modifier
                            .size(180.dp)
                            .scale(1.25f),
                        iterations = LottieConstants.IterateForever
                    )
                    Button(
                        onClick = {
                            onConfirmConnectionToDevice(uiState.payload)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray.copy(alpha = 0.15f),
                            contentColor = LocalContentColor.current
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_confirm_add_device),
                            style = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
        is AddDeviceDialogUiState.ProvideNetworkCredentials -> {
            DialogScaffold(
                onDismissRequest = onStopAddDevice,
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false
                )
            ) {
                var ssid: String by remember { mutableStateOf("") }
                var password: String by remember { mutableStateOf("") }
                var isPasswordVisible: Boolean by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.title_provide_network_credentials),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    OutlinedTextField(
                        value = ssid,
                        onValueChange = {
                            ssid = it
                        },
                        singleLine = true,
                        label = {
                            Text(text = stringResource(id = R.string.label_input_ssid))
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        singleLine = true,
                        label = {
                            Text(text = stringResource(id = R.string.label_input_password))
                        },
                        visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            val iconPainter: Painter = if (isPasswordVisible) {
                                painterResource(id = R.drawable.ic_twotone_visibility_24)
                            } else {
                                painterResource(id = R.drawable.ic_twotone_visibility_off_24)
                            }
                            IconButton(
                                onClick = {
                                    isPasswordVisible = !isPasswordVisible
                                }
                            ){
                                Icon(
                                    painter = iconPainter,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    Button(
                        onClick = {
                            onProvideNetworkCredentials(
                                uiState.payload,
                                WifiCredentials(
                                    ssid = ssid,
                                    password = password
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray.copy(alpha = 0.15f),
                            contentColor = LocalContentColor.current
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_start_pair_device),
                            style = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
        is AddDeviceDialogUiState.Connecting -> {
            DialogScaffold(
                onDismissRequest = onStopAddDevice,
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                isShowCloseAction = false
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.title_connecting_device),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.hint_connecting_device),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.lottie_processing)
                    )
                    LottieAnimation(
                        composition = composition,
                        modifier = Modifier.size(180.dp),
                        iterations = LottieConstants.IterateForever
                    )
                }
            }
        }
        is AddDeviceDialogUiState.Success -> {

        }
        else -> Unit
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











