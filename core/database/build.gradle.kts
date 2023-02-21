plugins {
    id("einvoice.android.library")
    id("einvoice.android.room")
    alias(libs.plugins.kotlin.serialization) apply true
}

android {
    namespace = "com.example.database"
}

dependencies {
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.kotlinx.serialization.json)
}