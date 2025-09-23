package test

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import utils.androidTestImplementation
import utils.catalog
import utils.testImplementation
import utils.testRuntimeOnly

/**
 * Feature 모듈에서 공통으로 사용하는 테스트 라이브러리 모음 컨벤션 플러그인
 * unitTest, androidTest 모두 포함
 */
class FeatureTestConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            extensions.configure<LibraryExtension> {
                packaging {
                    resources {
                        excludes += "META-INF/LICENSE.md"
                        excludes += "META-INF/LICENSE-notice.md"
                    }
                }
            }
            dependencies {
                androidTestImplementation(catalog.findBundle("android-test").get())
                androidTestImplementation(catalog.findBundle("test-unit").get())
                testImplementation(catalog.findBundle("test-unit").get())
                testImplementation(catalog.findBundle("test-coroutine").get())
                testImplementation(catalog.findBundle("orbit-test").get())
                testImplementation(catalog.findBundle("kotest").get())
                testRuntimeOnly(catalog.findBundle("test-runtime").get())

                testImplementation(catalog.findLibrary("mockk").get())
                androidTestImplementation(catalog.findLibrary("mockk-android").get())
            }

            tasks.withType<Test> {
                useJUnitPlatform()
            }
        }
    }
}