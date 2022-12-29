val composeVersion by extra { "1.2.1" }
val koinVersion by extra { "3.2.2" }
val koinComposeVersion by extra { "3.2.1" }
val coroutinesVersion by extra { "1.6.4" }
val coilVersion by extra { "2.2.2" }
val navigationVersion by extra { "2.5.2" }
val truthVersion by extra { "1.1.3" }
val lifecycleVersion by extra { "2.6.0-alpha02" }
val roomVersion by extra { "2.4.3" }
val ktorVersion by extra { "2.1.1" }
val kotlinVersion by extra { "1.7.10" }

plugins {
    id(Plugins.androidApplication) version Versions.androidApplication apply false
    id(Plugins.androidLibrary) version Versions.androidLibrary apply false
    id(Plugins.kotlinAndroid) version Versions.kotlin apply false
    id(Plugins.kotlinJvm) version Versions.kotlin apply false
}