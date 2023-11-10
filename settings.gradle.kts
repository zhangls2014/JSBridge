pluginManagement {
    repositories {
        google()
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven("https://maven.aliyun.com/repository/public")
        maven("https://jitpack.io")
        mavenCentral()
    }
}

rootProject.name = "JSBridge"
include(":app")
include(":jsbridge")
