plugins {
    id("einvoice.android.feature")
}

android {
    namespace = "com.example.maploaction"
}

dependencies {
    implementation(libs.compose.maps)
    implementation(libs.google.play.services.maps)
    implementation(libs.google.play.services.location)
}