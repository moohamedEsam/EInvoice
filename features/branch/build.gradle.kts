plugins {
    id("einvoice.android.feature")
}

android {
    namespace = "com.example.branch"
}

dependencies {
    implementation(project(":features:mapLocation"))
    implementation(libs.compose.material.dialog.core)
    implementation(libs.compose.material.dialog.datetime)
}