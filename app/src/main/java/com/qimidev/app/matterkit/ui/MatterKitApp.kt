package com.qimidev.app.matterkit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.qimidev.app.matterkit.feature.main.mainRoute
import com.qimidev.app.matterkit.feature.main.mainScreen

@Composable
fun MatterKitApp(
    appState: MatterKitAppState = rememberMatterKitAppState()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        MatterKitNavHost(
            navHostController = appState.navHostController
        )
    }
}

@Composable
fun MatterKitNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = mainRoute
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        mainScreen()
    }
}











