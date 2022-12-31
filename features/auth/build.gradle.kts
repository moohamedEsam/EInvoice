plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinSerialization) version Versions.kotlinSerialization
}

android {
    namespace = "com.example.auth"
    compileSdk = Versions.compileSdk
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.compileSdk
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures.compose = true
    kotlinOptions.jvmTarget = "1.8"
    composeOptions.kotlinCompilerExtensionVersion = Versions.kotlinCompilerExtensionVersion

}

dependencies {
    implementation(Dependencies.composeUi)
    implementation(Dependencies.composeMaterial3)
    debugImplementation(Dependencies.composeUiTooling)
    implementation(Dependencies.composeUiToolingPreview)
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
    implementation(Dependencies.koinAndroid)
    implementation(Dependencies.koinCore)
    implementation(Dependencies.koinCompose)
    implementation(Dependencies.lifecycleRuntimeKtx)
    implementation(Dependencies.viewModelKtx)
    implementation(Dependencies.coroutinesAndroid)
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.viewModelCompose)
    implementation(project(":common"))

    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.truth)
    testImplementation(Dependencies.turbine)
    testImplementation(Dependencies.ktorTesting)
    testImplementation(Dependencies.coroutinesTest)
}