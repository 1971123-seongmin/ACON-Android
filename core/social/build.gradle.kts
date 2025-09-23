import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.acon.android.library)
    alias(libs.plugins.acon.android.library.hilt)
    alias(libs.plugins.acon.common.unit.test)
}

val localProperties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.acon.core.social"

    defaultConfig {
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${localProperties["GOOGLE_CLIENT_ID"]}\"")
    }
}

dependencies {

    implementation(projects.core.model)

    implementation(libs.bundles.googleSignIn)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
