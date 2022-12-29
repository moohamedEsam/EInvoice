plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinSerialization) version Versions.kotlinSerialization
}

android {
    namespace = "com.example.auth"
    compileSdk = Versions.compileSdk
}

dependencies {
    implementation(Dependencies.composeUi)
    implementation(Dependencies.composeMaterial3)
    implementation(Dependencies.extendedIcons)
    implementation(Dependencies.ktorClient)
    implementation(Dependencies.ktorAuthentication)
    implementation(Dependencies.ktorCIO)
    implementation(Dependencies.ktorClientLogging)
    implementation(Dependencies.ktorContentNegotiation)
    implementation(Dependencies.ktorKotlinSerialization)
    implementation(Dependencies.koin)
    implementation(Dependencies.koinCompose)
    implementation(Dependencies.lifecycleRuntimeKtx)
    implementation(Dependencies.viewModelKtx)
    implementation(Dependencies.coroutinesAndroid)
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.viewModelCompose)
    implementation(project(":common"))
}