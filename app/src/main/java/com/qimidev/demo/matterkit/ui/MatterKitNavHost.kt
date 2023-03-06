package com.qimidev.demo.matterkit.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.qimidev.demo.matterkit.main.mainRoute
import com.qimidev.demo.matterkit.main.mainScreen

@Composable
fun MatterKitNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = mainRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        mainScreen()
    }
}