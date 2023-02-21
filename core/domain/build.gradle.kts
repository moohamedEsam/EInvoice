plugins {
    id("einvoice.android.library")
}

android {
    namespace = "com.example.domain"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:worker"))
    implementation(libs.androidx.work.ktx)
    implementation(libs.androidx.paging.runtime)
}