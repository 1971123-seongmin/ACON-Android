import utils.androidTestImplementation
import utils.testImplementation
import java.util.Properties

plugins {
    alias(libs.plugins.acon.android.library)
    alias(libs.plugins.acon.android.feature)
    alias(libs.plugins.acon.android.library.compose)
    alias(libs.plugins.acon.android.library.hilt)
    alias(libs.plugins.acon.android.library.orbit)
    alias(libs.plugins.acon.android.library.haze)
    alias(libs.plugins.acon.android.library.coil)
}

val localProperties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.acon.acon.feature.profile"

    defaultConfig {
        buildConfigField("String", "BUCKET_URL", "\"${localProperties["BUCKET_URL"]}\"")
    }

    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(libs.google.services.ads) // TODO - admob Plugin 분리?
    implementation(libs.androidx.paignig.compose)

    testImplementation(libs.bundles.non.android.test)
    androidTestImplementation(libs.bundles.non.android.test)
    androidTestImplementation(libs.mockk.android)
    testRuntimeOnly(libs.bundles.junit5.runtime)
    androidTestImplementation(libs.bundles.android.test)
    testImplementation(libs.bundles.orbit.test)
    testImplementation(libs.bundles.kotest)
}

tasks.withType<Test> {
    useJUnitPlatform()
}