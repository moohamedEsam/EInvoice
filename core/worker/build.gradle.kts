plugins {
    id("einvoice.android.library")
}

android {
    namespace = "com.example.worker"
}

dependencies{
    implementation(libs.androidx.core.ktx)
    implementation(libs.koin.workmanager)
    implementation(libs.androidx.work.ktx)
    implementation(project(":core:data"))
}