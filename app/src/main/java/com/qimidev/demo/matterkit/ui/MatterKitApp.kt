package com.qimidev.demo.matterkit.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MatterKitApp() {
    MatterKitNavHost(
        navController = rememberNavController()
    )
}