plugins {
    id("matterkit.android.feature")
    id("matterkit.android.hilt")
}

android {
    namespace = "com.qimidev.app.matterkit.feature.main"
}

dependencies {
    implementation(project(":core-ui"))
    implementation(project(":core-matter"))
    implementation(project(":core-model"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.barcode.scanning)
}