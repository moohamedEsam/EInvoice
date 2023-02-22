import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("einvoice.android.library.compose")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("implementation", project(":features:EInvoiceComponents"))
                add("implementation", libs.findLibrary("androidx-paging-runtime").get())
                add("implementation", libs.findLibrary("androidx-paging-compose").get())
                addComposeDependencies(libs)
                add("implementation", project(":core:domain"))

                add("implementation", libs.findLibrary("coil.kt").get())
                add("implementation", libs.findLibrary("coil.kt.compose").get())
                add("implementation", libs.findLibrary("koin-compose").get())

                add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())


                add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())
                add("androidTestImplementation", libs.findLibrary("androidx.compose-ui-test").get())
            }
        }
    }
}

fun DependencyHandlerScope.addComposeDependencies(libs: VersionCatalog) {
    add("implementation", libs.findLibrary("androidx-compose-foundation").get())
    add("implementation", libs.findLibrary("androidx-compose-runtime").get())
    add("implementation", libs.findLibrary("accompanist-flowlayout").get())
    add("implementation", libs.findLibrary("accompanist-pager").get())
    add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
    add("implementation", libs.findLibrary("androidx-compose-material3").get())
    add("implementation", libs.findLibrary("androidx.compose.material.iconsExtended").get())
    add("implementation", libs.findLibrary("androidx-navigation-compose").get())
}

