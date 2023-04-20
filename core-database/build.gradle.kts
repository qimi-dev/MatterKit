plugins {
    id("matterkit.android.library")
    id("matterkit.android.hilt")
    id("matterkit.android.room")
}

android {
    namespace = "com.qimidev.app.matterkit.core.database"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {

}