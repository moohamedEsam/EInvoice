plugins {
    id("einvoice.android.library.compose")
}

android {
    namespace = "com.example.einvoicecomponents"
}

dependencies {
    implementation(libs.compose.material.dialog.core)
    implementation(libs.compose.material.dialog.datetime)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.androidx.paging.compose)
}