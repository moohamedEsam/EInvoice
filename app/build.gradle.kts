plugins {
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.ksp) version Versions.ksp
    kotlin(Plugins.kotlinSerialization) version "1.7.10"
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

    implementation(Dependencies.koin)
    implementation(Dependencies.koinCompose)

    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.coroutinesAndroid)

    implementation(Dependencies.viewModelKtx)
    implementation(Dependencies.viewModelCompose)

    implementation(Dependencies.coil)
    implementation(Dependencies.coilCompose)
    implementation(Dependencies.coilGifs)

    implementation(Dependencies.room)
    annotationProcessor(Dependencies.roomCompiler)
    ksp(Dependencies.roomKsp)
    implementation(Dependencies.roomKtx)

    implementation(Dependencies.ktorClientLogging)
    implementation(Dependencies.ktorClient)
    implementation(Dependencies.ktorCIO)
    implementation(Dependencies.ktorKotlinSerialization)
    implementation(Dependencies.ktorContentNegotiation)
    implementation(Dependencies.ktorAuthentication)
}