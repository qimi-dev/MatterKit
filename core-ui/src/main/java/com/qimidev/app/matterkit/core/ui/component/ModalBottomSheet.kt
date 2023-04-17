package com.qimidev.app.matterkit.core.ui.component

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qimidev.app.matterkit.core.ui.theme.MatterKitTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatterKitBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it == SheetValue.Expanded }
        ),
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        scrimColor = scrimColor,
        dragHandle = {
            ClickHandle(onClick = onDismissRequest)
        },
        content = {
            val systemUiController = rememberSystemUiController()
            DisposableEffect(systemUiController) {
                systemUiController.isNavigationBarVisible = true
                systemUiController.setNavigationBarColor(containerColor)
                onDispose {
                    systemUiController.isNavigationBarVisible = false
                    systemUiController.isNavigationBarContrastEnforced = false
                    systemUiController.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .draggable(
                        state = rememberDraggableState(onDelta = {}),
                        orientation = Orientation.Vertical,
                        enabled = true
                    )
            ) {
                content()
            }
        }
    )
}

@Preview
@Composable
private fun ClickHandlePreview() {
    MatterKitTheme {
        ClickHandle(onClick = {})
    }
}

@Composable
private fun ClickHandle(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .draggable(
                state = rememberDraggableState(onDelta = {}),
                orientation = Orientation.Vertical,
                enabled = true
            )
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(24.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Gray.copy(alpha = 0.15f),
                    contentColor = Color.Gray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}




