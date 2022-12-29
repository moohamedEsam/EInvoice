plugins {
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.ksp) version Versions.ksp
    kotlin(Plugins.kotlinSerialization) version Versions.kotlinSerialization
}

android {
    namespace = "com.example.einvoice"
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "com.example.einvoice"
        minSdk = 21
        targetSdk = Versions.compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures.compose = true
    kotlinOptions.jvmTarget = "1.8"
    composeOptions.kotlinCompilerExtensionVersion = "1.3.1"
    packagingOptions {
        resources.excludes += "META-INF/atomicfu.kotlin_module"
    }
}

dependencies {
    implementation(Dependencies.appCompact)
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.lifecycleRuntimeKtx)
    implementation(Dependencies.activityCompose)
    implementation(Dependencies.composeUi)
    implementation(Dependencies.composeUiToolingPreview)
    implementation(Dependencies.composeMaterial3)
    testImplementation(Dependencies.junit)
    debugImplementation(Dependencies.composeUiTooling)
    debugImplementation(Dependencies.composeUiTestManifest)

    implementation(Dependencies.extendedIcons)

    implementation(Dependencies.composeNavigation)

    implementation(Dependencies.splashScreen)

    implementation(project(":common"))
}