plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.rikka.tools.materialthemebuilder)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
}

kapt {
    correctErrorTypes = true
    
    arguments {
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
        arg("room.schemaLocation", "$projectDir/schemas".toString())
    }
}

materialThemeBuilder {
    themes {
        for ((name, color) in listOf(
            "Default" to "6750A4",
            "Red" to "F44336",
            "Pink" to "E91E63",
            "Purple" to "9C27B0",
            "DeepPurple" to "673AB7",
            "Indigo" to "3F51B5",
            "Blue" to "2196F3",
            "LightBlue" to "03A9F4",
            "Cyan" to "00BCD4",
            "Teal" to "009688",
            "Green" to "4FAF50",
            "LightGreen" to "8BC3A4",
            "Lime" to "CDDC39",
            "Yellow" to "FFEB3B",
            "Amber" to "FFC107",
            "Orange" to "FF9800",
            "DeepOrange" to "FF5722",
            "Brown" to "795548",
            "BlueGrey" to "607D8F",
            "Sakura" to "FF9CA8"
        )) {
            create("Material$name") {
                lightThemeFormat = "ThemeOverlay.Light.%s"
                darkThemeFormat = "ThemeOverlay.Dark.%s"
                primaryColor = "#$color"
            }
        }
    }
    // Add Material Design 3 color tokens (such as palettePrimary100) in generated theme
    // rikka.material >= 2.0.0 provides such attributes
    generatePalette = true
}

val gitBuildNumber = providers.exec {
    commandLine("git", "rev-list", "--count", "HEAD")
}.standardOutput.asText.get().trim().toInt()

val gitBuildName = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.get().trim()

android {
    namespace = "com.example.c001apk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.c001apk"
        minSdk = 24
        targetSdk = 34
        versionCode = gitBuildNumber
        versionName = gitBuildName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("keyStore") {
            keyAlias = "c001apk"
            keyPassword = "123456"
            storeFile = file("c001apk.jks")
            storePassword = "123456"
        }
    }

    buildTypes {
        val signConfig = signingConfigs.getByName("keyStore")
        getByName("release") {
            isCrunchPngs = false
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signConfig
        }
        getByName("debug") {
            isCrunchPngs = false
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signConfig
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    val SUPPORTED_ABIS = setOf(
        "armeabi-v7a", "arm64-v8a", "x86"
    )
    splits {
        abi {
            isEnable = true
            reset()
            include(*SUPPORTED_ABIS.toTypedArray())
            isUniversalApk = true
        }
    }
    defaultConfig {
        ndk {
            abiFilters += SUPPORTED_ABIS
        }
    }

    applicationVariants.all {
        outputs.all {
            val versionName = defaultConfig.versionName
            val versionCode = defaultConfig.versionCode
            if (buildType.name == "release" || buildType.name == "debug")
                (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                    (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName
                        .replace("app-","c001apk-")
                        .replace("release","($versionCode)")
                        .replace("debug","($versionCode)")
        }
    }
}

configurations.configureEach {
    exclude("androidx.appcompat", "appcompat")
}

dependencies {
    androidTestImplementation(libs.androidx.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.leakcanary.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.webkit)
    implementation(libs.androidx.core.ktx)
    implementation(libs.google.android.flexbox)
    implementation(libs.google.android.material)
    implementation(libs.google.dagger.hilt.android)
    kapt(libs.google.dagger.hilt.android.compiler)
    implementation(libs.rikkax.borderview)
    implementation(libs.rikkax.material.preference)
    implementation(libs.rikkax.material)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.glide)
    kapt(libs.glide.compiler)
    implementation(libs.glide.okhttp3.integration)
    implementation(libs.glide.transformations)
    implementation("com.github.tulip799837434.mojito:mojito:1.0.5")
    implementation("com.github.tulip799837434.mojito:SketchImageViewLoader:1.0.5")
    implementation("com.github.tulip799837434.mojito:GlideImageLoader:1.0.5")
    implementation(libs.drakeet.about)
    implementation(libs.jbcrypt)
    implementation(libs.jsoup)
    implementation(libs.zhaobozhen.libraries.utils)
    testImplementation(libs.junit)
}