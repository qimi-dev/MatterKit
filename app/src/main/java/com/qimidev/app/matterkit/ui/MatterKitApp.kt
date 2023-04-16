package com.qimidev.app.matterkit.ui

import android.view.WindowInsetsController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qimidev.app.matterkit.R
import com.qimidev.app.matterkit.ui.theme.MatterKitTheme

@Composable
fun MatterKitApp(
    appState: MatterKitAppState = rememberMatterKitAppState()
) {
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        systemUiController.isNavigationBarVisible = false
        systemUiController.isNavigationBarContrastEnforced = false
        systemUiController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    Scaffold(
        topBar = {
            MatterKitTopAppBar()
        }
    ) { contentPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
        ) {
            MatterKitNavHost(
                navHostController = appState.navHostController
            )
        }
    }
}

@Preview()
@Composable
fun MatterKitTopAppBarPreview() {
    MatterKitTheme {
        MatterKitTopAppBar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatterKitTopAppBar() {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun MatterKitNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = ""
) {
//    NavHost(
//        navController = navHostController,
//        startDestination = startDestination,
//        modifier = modifier
//    ) {
//
//    }
}











