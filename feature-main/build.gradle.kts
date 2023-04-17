plugins {
    id("matterkit.android.feature")
    id("matterkit.android.hilt")
}

android {
    namespace = "com.qimidev.app.matterkit.feature.main"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
}