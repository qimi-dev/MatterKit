plugins {
    id("matterkit.android.library")
    id("matterkit.android.hilt")
}

android {
    namespace = "com.qimidev.app.matterkit.core.matter"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets.getByName("main").jniLibs.srcDir("libs/jniLibs")
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))
    implementation(project(":core-model"))
}