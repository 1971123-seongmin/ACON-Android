plugins {
    alias(libs.plugins.acon.non.android.library)
    alias(libs.plugins.acon.common.unit.test)
}

dependencies {
    api(projects.core.model)

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}

tasks.withType<Test> {
    useJUnitPlatform()
}