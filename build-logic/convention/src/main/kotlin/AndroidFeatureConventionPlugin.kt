import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("matterkit.android.library")
                apply("matterkit.android.library.compose")
            }
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    defaultConfig {
                        testInstrumentationRunner =
                            "androidx.test.runner.AndroidJUnitRunner"
                    }
                }
            }
        }
    }

}