pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "EInvoice"
include(":app")
include(":features:auth")
include(":common")
include(":core:network")
include(":core:data")
include(":core:models")
include(":core:database")
include(":core:domain")
include(":features:company")
include(":features:mapLocation")
include(":features:branch")
