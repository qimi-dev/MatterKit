package com.qimidev.app.matterkit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberMatterKitAppState(
    navHostController: NavHostController = rememberNavController()
): MatterKitAppState {
    return remember {
        MatterKitAppState(
            navHostController = navHostController
        )
    }
}

class MatterKitAppState(
    val navHostController: NavHostController
)