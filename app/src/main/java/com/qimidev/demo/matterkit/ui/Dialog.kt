package com.qimidev.demo.matterkit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DialogScaffold(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    containerColor: Color = Color.White,
    isShowCloseAction: Boolean = true,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = containerColor
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (isShowCloseAction) {
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .align(Alignment.End)
                            .size(24.dp),
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
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    content()
                }
            }
        }
    }
}