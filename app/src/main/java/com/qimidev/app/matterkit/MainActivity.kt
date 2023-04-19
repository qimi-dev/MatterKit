package com.qimidev.app.matterkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qimidev.app.matterkit.ui.MatterKitApp
import com.qimidev.app.matterkit.core.ui.theme.MatterKitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var systemUiController: SystemUiController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MatterKitTheme {
                systemUiController = rememberSystemUiController()
                val primaryColor: Color = MaterialTheme.colorScheme.background
                SideEffect {
                    systemUiController!!.isNavigationBarVisible = false
                    systemUiController!!.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    systemUiController!!.setStatusBarColor(primaryColor)
                }

                MatterKitApp()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        systemUiController?.isNavigationBarVisible = false
    }

}
