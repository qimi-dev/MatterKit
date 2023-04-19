package com.qimidev.app.matterkit.feature.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.qimidev.app.matterkit.core.ui.component.MatterKitBottomDialog
import com.qimidev.app.matterkit.core.ui.theme.MatterKitTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val setupDevicePermissions: List<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun MainRoute(
    viewModel: MainViewModel = hiltViewModel()
) {
    var isShowSetupDevicePermissionsRationale: Boolean by remember { mutableStateOf(false) }
    var isMeetMinimumRequirements: Boolean by remember { mutableStateOf(false) }
    val setupDevicePermissionsState = rememberMultiplePermissionsState(
        permissions = setupDevicePermissions
    ) {
        if (it.all { it.value }) {
            // TODO setup device
        } else {
            isShowSetupDevicePermissionsRationale = true
        }
    }
    if (isShowSetupDevicePermissionsRationale) {
        SetupDevicePermissionsRationale(
            onDismissRequest = {
                isShowSetupDevicePermissionsRationale = false
            },
            onMeetMinimumRequirements = {
                isMeetMinimumRequirements = true
                // TODO setup device
            },
            permissionsState = setupDevicePermissionsState
        )
    }
    Scaffold(
        topBar = {
            MainTopAppBar(
                onAddDevice = {
                    // Check if the app has permissions
                    if (setupDevicePermissionsState.allPermissionsGranted) {
                        // TODO setup device
                    } else if (isMeetMinimumRequirements) {
                        // TODO setup device
                    } else {
                        if (setupDevicePermissionsState.shouldShowRationale) {
                            isShowSetupDevicePermissionsRationale = true
                        } else {
                            // Direct request
                            setupDevicePermissionsState.launchMultiplePermissionRequest()
                        }
                    }
                }
            )
        }
    ) { contentPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
        ) {
            MainScreen()
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun MainTopAppBarPreview() {
    MatterKitTheme {
        MainTopAppBar(onAddDevice = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopAppBar(
    onAddDevice: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.main_page_title))
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        },
        actions = {
            IconButton(onClick = onAddDevice) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun MainScreen() {

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun SetupDevicePermissionsRationale(
    onDismissRequest: () -> Unit,
    onMeetMinimumRequirements: () -> Unit,
    permissionsState: MultiplePermissionsState
) {
    val context: Context = LocalContext.current
    MatterKitBottomDialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.setup_device_permission_tips),
                style = MaterialTheme.typography.titleSmall
            )
            permissionsState.revokedPermissions.forEach {
                when (it.permission) {
                    Manifest.permission.CAMERA -> {
                        PermissionSpecification(
                            icon = painterResource(id = R.drawable.ic_camera_24),
                            permission = stringResource(id = R.string.camera_permission),
                            description = stringResource(id = R.string.camera_permission_description),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Manifest.permission.ACCESS_COARSE_LOCATION -> {
                        PermissionSpecification(
                            icon = painterResource(id = R.drawable.ic_local_24),
                            permission = stringResource(id = R.string.location_permission),
                            description = stringResource(id = R.string.location_permission_description),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Manifest.permission.BLUETOOTH_SCAN -> {
                        PermissionSpecification(
                            icon = painterResource(id = R.drawable.ic_search_24),
                            permission = stringResource(id = R.string.search_permission),
                            description = stringResource(id = R.string.search_permission_description),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onDismissRequest()
                        val isMeetMinimumRequirements: Boolean
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            isMeetMinimumRequirements = permissionsState.revokedPermissions
                                .all {
                                    it.permission != Manifest.permission.BLUETOOTH_SCAN &&
                                        it.permission != Manifest.permission.BLUETOOTH_CONNECT
                                }
                        } else {
                            isMeetMinimumRequirements = permissionsState.revokedPermissions
                                .all {
                                    it.permission != Manifest.permission.ACCESS_FINE_LOCATION &&
                                            it.permission != Manifest.permission.ACCESS_COARSE_LOCATION
                                }
                        }
                        if (isMeetMinimumRequirements) {
                            onMeetMinimumRequirements()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.cancel_request_permission))
                }
                Button(
                    onClick = {
                        onDismissRequest()
                        if (permissionsState.shouldShowRationale) {
                            permissionsState.launchMultiplePermissionRequest()
                        } else {
                            // 打开系统设置权限页面
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                setData(Uri.fromParts("package", context.packageName, null))
                                context.startActivity(this)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.confirm_request_permission))
                }
            }
        }
    }
}

@Composable
private fun PermissionSpecification(
    icon: Painter,
    permission: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = icon, contentDescription = null)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = permission,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray
                )
            )
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










