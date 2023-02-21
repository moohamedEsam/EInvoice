import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("einvoice.android.library")
    alias(libs.plugins.protobuf) apply true
}

android {
    namespace = "com.example.network"
}

dependencies {
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.serialization)
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.protobuf.protoc)
    implementation(libs.protobuf.kotlin.lite)
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