plugins {
    id("einvoice.android.library")
    id("einvoice.android.test")
}

android {
    namespace = "com.example.data"
}

dependencies {
    implementation(libs.androidx.paging.runtime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    androidTestImplementation(libs.room.runtime)
}