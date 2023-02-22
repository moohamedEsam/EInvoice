plugins {
    id("einvoice.android.feature")
}

android {
    namespace = "com.example.company"
}

dependencies{
    implementation(libs.compose.material.dialog.core)
    implementation(libs.compose.material.dialog.datetime)
}