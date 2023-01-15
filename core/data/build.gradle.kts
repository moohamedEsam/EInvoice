plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
}

android {
    namespace = "com.example.data"
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

    kotlinOptions.jvmTarget = "1.8"
    composeOptions.kotlinCompilerExtensionVersion = Versions.kotlinCompilerExtensionVersion
}

dependencies{
    implementation(Dependencies.koinAndroid)
    implementation(Dependencies.koinCore)
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.coroutinesAndroid)
    implementation(project(":core:models"))
    implementation(project(":core:network"))
    implementation(project(":common"))
    implementation(project(":core:database"))

}