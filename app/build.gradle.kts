import java.util.Properties
plugins {
    id("einvoice.android.application")
    id("einvoice.android.application.compose")
    alias(libs.plugins.protobuf) apply true
}

android {
    namespace = "com.example.einvoice"

    defaultConfig {
        applicationId = "com.example.einvoice"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val prop = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = prop.getProperty("GOOGLE_MAPS_API_KEY")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }

    packagingOptions {
        resources.excludes += "META-INF/atomicfu.kotlin_module"
    }
}

dependencies {

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.coil.kt)
    implementation(libs.coil.gif)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.workmanager)
    implementation(libs.koin.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.work.ktx)
    implementation(libs.androidx.dataStore.core)
    implementation(libs.protobuf.kotlin.lite)

    implementation(project(":common"))
    implementation(project(":core:network"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:worker"))
    implementation(project(":features:auth"))
    implementation(project(":features:company"))
    implementation(project(":features:mapLocation"))
    implementation(project(":features:branch"))
    implementation(project(":features:client"))
    implementation(project(":features:item"))
    implementation(project(":features:EInvoiceComponents"))
    implementation(project(":features:document"))
}