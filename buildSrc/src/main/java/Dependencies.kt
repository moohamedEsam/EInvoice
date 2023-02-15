object Plugins {
    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val kotlinAndroid = "org.jetbrains.kotlin.android"
    const val kotlinJvm = "org.jetbrains.kotlin.jvm"
    const val kotlinSerialization = "plugin.serialization"
    const val ksp = "com.google.devtools.ksp"
}


object Dependencies {
    const val appCompact = "androidx.appcompat:appcompat:${Versions.appCompact}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.androidX}"

    const val composeUi = "androidx.compose.ui:ui:${Versions.composeVersion}"
    const val composeUiToolingPreview =
        "androidx.compose.ui:ui-tooling-preview:${Versions.composeVersion}"
    const val composeMaterial3 = "androidx.compose.material3:${Versions.composeMaterial3}"
    const val composeUiTestJunit4 = "androidx.compose.ui:ui-test-junit4:${Versions.composeVersion}"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.composeVersion}"
    const val composeUiTestManifest =
        "androidx.compose.ui:ui-test-manifest:${Versions.composeVersion}"
    const val extendedIcons =
        "androidx.compose.material:material-icons-extended:${Versions.composeVersion}"
    const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
    const val composeNavigation =
        "androidx.navigation:navigation-compose:${Versions.composeNavigation}"

    const val lifecycleRuntimeKtx =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifeCycleRuntimeKtx}"
    const val viewModelCompose =
        "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifeCycleRuntimeKtx}"
    const val viewModelKtx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifeCycleRuntimeKtx}"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}"
    const val coroutinesTest =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesVersion}"

    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashScreen}"
    const val coil = "io.coil-kt:coil:${Versions.coil}"
    const val coilCompose = "io.coil-kt:coil-compose:${Versions.coil}"
    const val coilGifs = "io.coil-kt:coil-gif:${Versions.coil}"

    const val room = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomKsp = "androidx.room:room-compiler:${Versions.room}"
    const val roomTesting = "androidx.room:room-testing:${Versions.room}"

    const val ktorClient = "io.ktor:ktor-client-android:${Versions.ktorVersion}"
    const val ktorCIO = "io.ktor:ktor-client-cio:${Versions.ktorVersion}"
    const val ktorClientLogging = "io.ktor:ktor-client-logging:${Versions.ktorVersion}"
    const val ktorContentNegotiation =
        "io.ktor:ktor-client-content-negotiation:${Versions.ktorVersion}"
    const val ktorKotlinSerialization =
        "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktorVersion}"
    const val ktorAuthentication = "io.ktor:ktor-client-auth:${Versions.ktorVersion}"
    const val ktorTesting = "io.ktor:ktor-client-mock:${Versions.ktorVersion}"

    const val composeMaps = "com.google.maps.android:maps-compose:2.7.2"
    const val maps = "com.google.android.gms:play-services-maps:18.1.0"
    const val location = "com.google.android.gms:play-services-location:20.0.0"

    const val composePager =
        "com.google.accompanist:accompanist-pager:${Versions.accompanistVersion}"
    const val composePagerIndicators =
        "com.google.accompanist:accompanist-pager-indicators:${Versions.accompanistVersion}"
    const val composeFlowLayout =
        "com.google.accompanist:accompanist-flowlayout:${Versions.accompanistVersion}"

    const val materialDialogCore = "io.github.vanpra.compose-material-dialogs:core:${Versions.materialDialogsVersion}"
    const val materialDialogsDateTime = "io.github.vanpra.compose-material-dialogs:datetime:${Versions.materialDialogsVersion}"

    const val workManager = "androidx.work:work-runtime-ktx:${Versions.workVersion}"

    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val androidXtestCore = "androidx.test:core:${Versions.androidXTest}"
    const val androidXtestRunner = "androidx.test:runner:${Versions.androidXTestRunner}"
    const val androidXtestRules = "androidx.test:rules:${Versions.androidXTestRunner}"
    const val junit = "junit:junit:${Versions.jUnit}"
    const val junitExt = "androidx.test.ext:junit:${Versions.jUnitExt}"
    const val truth = "com.google.truth:truth:${Versions.truth}"
    const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"

    const val koinAndroid = "io.insert-koin:koin-android:${Versions.koin}"
    const val koinCore = "io.insert-koin:koin-core:${Versions.koin}"
    const val koinCompose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
    const val koinWorkManager = "io.insert-koin:koin-androidx-workmanager:${Versions.koin}"

    const val baselineProfile ="androidx.profileinstaller:profileinstaller:${Versions.baselineProfile}"
}