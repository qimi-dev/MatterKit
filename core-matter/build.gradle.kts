plugins {
    id("matterkit.android.library")
}

android {
    namespace = "com.qimidev.app.matterkit.core.matter"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets.getByName("main").jniLibs.srcDir("libs/jniLibs")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))
}