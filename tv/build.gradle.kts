import com.github.takahirom.roborazzi.ExperimentalRoborazziApi

plugins {
    id("com.costular.jellydroid.android.application.compose")
    id("com.costular.jellydroid.android.application")
    id("com.costular.jellydroid.android.hilt")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.costular.jellydroid.tv"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.costular.jellydroid.tv"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
                // For large preview
                it.maxHeapSize = "4096m"
                it.jvmArgs("-noverify")
            }
        }
    }
}

roborazzi {
    @OptIn(ExperimentalRoborazziApi::class)
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("com.costular.jellydroid")
        robolectricConfig = mapOf(
            "sdk" to "[35]",
            "qualifiers" to "RobolectricDeviceQualifiers.Television4K",
        )
        includePrivatePreviews = true
    }
}

dependencies {
    implementation(projects.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.bundles.roborazzi)
    testImplementation(libs.composable.preview.scanner)
}