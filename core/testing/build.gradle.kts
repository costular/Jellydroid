plugins {
    id("com.costular.jellydroid.android.library")
    id("com.costular.jellydroid.android.library.compose")
    id("kotlin-android")
    id("com.costular.jellydroid.android.hilt")
}

android {
    namespace = "com.costular.jellydroid.core.testing"
}

dependencies {
    api(libs.kotest.assertion.core)
    implementation(projects.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.hilt.android.testing)
    implementation(libs.androidx.test.runner)

    implementation(libs.androidx.junit)
    implementation(libs.junit)
    implementation(libs.androidx.compose.ui.test)
}