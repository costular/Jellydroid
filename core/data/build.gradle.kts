plugins {
    id("com.costular.jellydroid.android.library")
    id("com.costular.jellydroid.android.hilt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.costular.jellydroid.core.data"
}

dependencies {
    implementation(projects.core)
    api(projects.core.model)
    implementation(libs.jellyfin.sdk)
    implementation(libs.arrow.core)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    testImplementation(projects.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}