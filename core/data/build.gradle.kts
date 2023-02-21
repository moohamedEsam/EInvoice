plugins {
    id("einvoice.android.library")
}

android {
    namespace = "com.example.data"
}

dependencies {
    implementation(libs.androidx.paging.runtime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(project(":core:network"))
    implementation(project(":core:database"))
}