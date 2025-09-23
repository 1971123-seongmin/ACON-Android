import java.util.Properties

plugins {
    alias(libs.plugins.acon.android.library)
    alias(libs.plugins.acon.android.feature)
    alias(libs.plugins.acon.android.library.compose)
    alias(libs.plugins.acon.android.library.hilt)
    alias(libs.plugins.acon.android.library.orbit)
    alias(libs.plugins.acon.android.library.haze)
    alias(libs.plugins.acon.android.library.coil)
    alias(libs.plugins.acon.feature.test)
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
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(libs.google.services.ads) // TODO - admob Plugin 분리?
    implementation(libs.androidx.paignig.compose)
}

tasks.withType<Test> {
    useJUnitPlatform()
}