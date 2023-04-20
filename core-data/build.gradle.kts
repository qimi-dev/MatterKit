plugins {
    id("matterkit.android.library")
    id("matterkit.android.hilt")
}

android {
    namespace = "com.qimidev.app.matterkit.core.data"
    compileSdk = 33

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-database"))
    implementation(project(":core-matter"))
}