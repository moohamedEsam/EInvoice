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
        minSdk = Versions.minSdk
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
    composeOptions.kotlinCompilerExtensionVersion = "1.3.2"
    packagingOptions {
        resources.excludes += "META-INF/atomicfu.kotlin_module"
    }
}

dependencies {
    implementation(Dependencies.appCompact)
    implementation(Dependencies.composeUi)
    implementation(Dependencies.composeMaterial3)
    debugImplementation(Dependencies.composeUiTooling)
    implementation(Dependencies.composeUiToolingPreview)
    implementation(Dependencies.activityCompose)
    implementation(Dependencies.composeNavigation)
    implementation(Dependencies.extendedIcons)
    implementation(Dependencies.coil)
    implementation(Dependencies.coilCompose)
    implementation(Dependencies.coilGifs)
    implementation(Dependencies.ktorClient)
    implementation(Dependencies.ktorAuthentication)
    implementation(Dependencies.ktorCIO)
    implementation(Dependencies.ktorClientLogging)
    implementation(Dependencies.ktorContentNegotiation)
    implementation(Dependencies.ktorKotlinSerialization)
    implementation(Dependencies.koinCore)
    implementation(Dependencies.koinAndroid)
    implementation(Dependencies.koinCompose)
    implementation(Dependencies.lifecycleRuntimeKtx)
    implementation(Dependencies.viewModelKtx)
    implementation(Dependencies.coroutinesAndroid)
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.viewModelCompose)
    implementation(Dependencies.splashScreen)

    implementation(project(":common"))
    implementation(project(":features:auth"))
    implementation(project(":features:company"))
    implementation(project(":core:network"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
}