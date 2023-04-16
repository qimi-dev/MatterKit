plugins {
    id("matterkit.android.library")
    id("matterkit.android.library.compose")
}

android {
    namespace = "com.qimidev.app.matterkit.core.ui"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(libs.androidx.compose.material3)
    debugApi(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)
}