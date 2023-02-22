import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.dependencies) {
            val libs = target.extensions.getByType<VersionCatalogsExtension>().named("libs")
            add("api", libs.findLibrary("androidx-test-core").get())
            add("api", libs.findLibrary("androidx-test-ext").get())
            add("api", libs.findLibrary("androidx-test-espresso-core").get())
            add("api", libs.findLibrary("androidx-test-runner").get())
            add("api", libs.findLibrary("androidx-test-rules").get())
            add("api", libs.findLibrary("google-truth").get())
            add("api", libs.findLibrary("turbine").get())
            add("api", libs.findLibrary("kotlinx-coroutines-test").get())
        }
    }
}