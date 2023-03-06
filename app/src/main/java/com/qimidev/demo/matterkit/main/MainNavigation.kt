package com.qimidev.demo.matterkit.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val mainRoute: String = "main"

fun NavGraphBuilder.mainScreen() {
    composable(route = mainRoute) {
        MainRoute()
    }
}

fun NavController.navigateToMain() = navigate(mainRoute)

