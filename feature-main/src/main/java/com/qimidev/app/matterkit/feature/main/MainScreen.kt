package com.qimidev.app.matterkit.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qimidev.app.matterkit.core.ui.component.MatterKitBottomDialog
import com.qimidev.app.matterkit.core.ui.theme.MatterKitTheme

@Composable
internal fun MainRoute(
    viewModel: MainViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            MainTopAppBar()
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
            MainScreen()
        }
    }
}

@Preview
@Composable
private fun MainTopAppBarPreview() {
    MatterKitTheme {
        MainTopAppBar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopAppBar() {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.main_page_title))
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
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun MainScreen() {
    var isShow: Boolean by remember {
        mutableStateOf(false)
    }
    Button(
        onClick = { isShow = true }
    ) {

    }
    if (isShow) {
        MatterKitBottomDialog(
            onDismissRequest = {
                isShow = false
            }
        ) {
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.align(Alignment.End)
            ) {

            }
        }
    }
}












