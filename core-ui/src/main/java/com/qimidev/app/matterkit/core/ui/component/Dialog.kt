package com.qimidev.app.matterkit.core.ui.component

import android.util.Log
import android.view.Gravity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MatterKitBottomDialog(
    onDismissRequest: () -> Unit,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
            usePlatformDefaultWidth = false
        )
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setGravity(Gravity.BOTTOM)
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.isNavigationBarVisible = false
            systemUiController.isNavigationBarContrastEnforced = false
            systemUiController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        WindowFocusChangeHandler {
            systemUiController.isNavigationBarVisible = false
        }

        Card(
            modifier = Modifier
                .padding(6.dp)
                .widthIn(max = 512.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (dismissOnBackPress) {
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp)
                            .size(24.dp)
                            .align(Alignment.End),
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
                } else {
                    Spacer(
                        modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp)
                            .size(24.dp)
                            .align(Alignment.End)
                    )
                }
                content()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}











