import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.proto) version Versions.protoPlugin
    kotlin(Plugins.kotlinSerialization) version Versions.kotlinSerialization
}

android {
    namespace = "com.example.network"
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

dependencies {
    implementation(Dependencies.ktorCIO)
    implementation(Dependencies.ktorClientLogging)
    implementation(Dependencies.ktorContentNegotiation)
    implementation(Dependencies.ktorKotlinSerialization)
    implementation(Dependencies.ktorAuthentication)
    implementation(Dependencies.koinAndroid)
    implementation(Dependencies.koinCore)
    implementation(Dependencies.dataStore)
    implementation(Dependencies.proto)
    implementation(project(":core:models"))
    implementation(project(":common"))
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.10.0"
    }

    generateProtoTasks {
        all().forEach {
            it.plugins{
                create("java"){
                    option("lite")
                }
            }
        }
    }
}