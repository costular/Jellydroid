plugins {
    id("com.costular.jellydroid.android.application.compose")
    id("com.costular.jellydroid.android.application")
}

android {
    namespace = "com.costular.jellydroid"

    defaultConfig {
        applicationId = "com.costular.jellydroid"
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.core)
}
