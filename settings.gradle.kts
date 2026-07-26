pluginManagement {
    repositories {
        mavenLocal()
        maven (url = "https://maven.aliyun.com/repository/public")
        maven (url = "https://maven.aliyun.com/repository/gradle-plugin")
        maven (url = "https://maven.aliyun.com/repository/google")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven (url = "https://maven.aliyun.com/repository/public")
        maven (url = "https://maven.aliyun.com/repository/gradle-plugin")
        maven (url = "https://maven.aliyun.com/repository/google")
        google()
        mavenCentral()
        maven ("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/public")
        gradlePluginPortal()
    }
}

rootProject.name = "c001apk"
include(":app")
