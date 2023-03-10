pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    includeBuild("build-logic")
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
include(":core:worker")
include(":features:EInvoiceComponents")
include(":features:client")
include(":features:item")
include(":features:document")
include(":benchmark")
