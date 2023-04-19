package com.qimidev.app.matterkit.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

@Composable
fun WindowFocusChangeHandler(block: (Boolean) -> Unit) {
    val viewTreeObserver = LocalView.current.viewTreeObserver
    DisposableEffect(block) {
        viewTreeObserver.addOnWindowFocusChangeListener(block)
        onDispose {
            viewTreeObserver.removeOnWindowFocusChangeListener(block)
        }
    }
}
