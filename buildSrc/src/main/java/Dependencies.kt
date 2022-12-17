object Plugins {
    val androidApplication by lazy { "com.android.application" }
    val androidLibrary by lazy { "com.android.library" }
    val kotlinAndroid by lazy { "org.jetbrains.kotlin.android" }
    val kotlinJvm by lazy { "org.jetbrains.kotlin.jvm" }
    val gradleDoctor by lazy { "com.osacky.doctor" }
    val kotlinSerialization by lazy { "plugin.serialization" }
    val ksp by lazy { "com.google.devtools.ksp" }
}


object Dependencies {
    val appCompact by lazy { "androidx.appcompat:appcompat:${Versions.appCompact}" }
    val coreKtx by lazy { "androidx.core:core-ktx:${Versions.androidX}" }

    val composeUi by lazy { "androidx.compose.ui:ui:${Versions.composeVersion}" }
    val composeUiToolingPreview by lazy { "androidx.compose.ui:ui-tooling-preview:${Versions.composeVersion}" }
    val composeMaterial3 by lazy { "androidx.compose.material3:${Versions.composeMaterial3}" }
    val composeUiTestJunit4 by lazy { "androidx.compose.ui:ui-test-junit4:${Versions.composeVersion}" }
    val composeUiTooling by lazy { "androidx.compose.ui:ui-tooling:${Versions.composeVersion}" }
    val composeUiTestManifest by lazy { "androidx.compose.ui:ui-test-manifest:${Versions.composeVersion}" }
    val extendedIcons by lazy { "androidx.compose.material:material-icons-extended:${Versions.composeVersion}" }
    val activityCompose by lazy { "androidx.activity:activity-compose:${Versions.activityCompose}" }
    val composeNavigation by lazy { "androidx.navigation:navigation-compose:${Versions.composeNavigation}" }

    val lifecycleRuntimeKtx by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifeCycleRuntimeKtx}" }
    val viewModelCompose by lazy { "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifeCycleRuntimeKtx}" }
    val viewModelKtx by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifeCycleRuntimeKtx}" }
    val coroutinesCore by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}" }
    val coroutinesAndroid by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}" }
    val coroutinesTest by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesVersion}" }

    val splashScreen by lazy { "androidx.core:core-splashscreen:${Versions.splashScreen}" }
    val coil by lazy { "io.coil-kt:coil:${Versions.coil}" }
    val coilCompose by lazy { "io.coil-kt:coil-compose:${Versions.coil}" }
    val coilGifs by lazy { "io.coil-kt:coil-gif:${Versions.coil}" }

    val room by lazy { "androidx.room:room-runtime:${Versions.room}" }
    val roomKtx by lazy { "androidx.room:room-ktx:${Versions.room}" }
    val roomCompiler by lazy { "androidx.room:room-compiler:${Versions.room}" }
    val roomKsp by lazy { "androidx.room:room-compiler:${Versions.room}" }
    val roomTesting by lazy { "androidx.room:room-testing:${Versions.room}" }

    val ktorClient by lazy { "io.ktor:ktor-client-android:${Versions.ktorVersion}" }
    val ktorCIO by lazy { "io.ktor:ktor-client-cio:${Versions.ktorVersion}" }
    val ktorClientLogging by lazy { "io.ktor:ktor-client-logging:${Versions.ktorVersion}" }
    val ktorContentNegotiation by lazy { "io.ktor:ktor-client-content-negotiation:${Versions.ktorVersion}" }
    val ktorKotlinSerialization by lazy { "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktorVersion}" }
    val ktorAuthentication by lazy { "io.ktor:ktor-client-auth:${Versions.ktorVersion}" }

    val junit by lazy { "junit:junit:${Versions.jUnit}" }
    val truth by lazy { "com.google.truth:truth:${Versions.truth}" }
    val turbine by lazy { "app.cash.turbine:turbine:${Versions.turbine}" }


    val koin by lazy { "io.insert-koin:koin-android:${Versions.koin}" }
    val koinCompose by lazy { "io.insert-koin:koin-androidx-compose:${Versions.koin}" }
}