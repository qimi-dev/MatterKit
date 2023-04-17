package com.qimidev.app.matterkit.feature.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val mainRoute = "main"

fun NavGraphBuilder.mainScreen() {
    composable(route = mainRoute) {
        MainRoute()
    }
}

