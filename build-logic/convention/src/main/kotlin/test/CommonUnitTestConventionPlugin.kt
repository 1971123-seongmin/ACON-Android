package test

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import utils.catalog
import utils.testImplementation
import utils.testRuntimeOnly

/**
 * androidTest를 하지 않는 모듈에서 공통으로 사용하는 unitTest 라이브러리 모음 컨벤션 플러그인
 */
class CommonUnitTestConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            dependencies {
                testImplementation(catalog.findBundle("test-unit").get())
                testImplementation(catalog.findBundle("test-coroutine").get())
                testImplementation(catalog.findBundle("kotest").get())
                testRuntimeOnly(catalog.findBundle("test-runtime").get())

                testImplementation(catalog.findLibrary("mockk").get())
            }

            tasks.withType<Test> {
                useJUnitPlatform()
            }
        }
    }
}