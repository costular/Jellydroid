plugins {
    id("com.costular.jellydroid.android.library")
    id("com.costular.jellydroid.android.hilt")
}

android {
    namespace = "com.costular.jellydroid.core"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}