package com.acon.feature.profile.update.viewmodel

import com.acon.acon.core.model.model.profile.BirthDateStatus
import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import com.acon.acon.domain.error.profile.ValidateNicknameError
import com.acon.acon.domain.repository.ProfileRepository
import com.acon.acon.domain.usecase.ValidateNicknameUseCase
import com.acon.feature.profile.update.status.NicknameValidationStatus
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.orbitmvi.orbit.test.test
import java.time.LocalDate

class ProfileUpdateViewModelTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    lateinit var profileRepository: ProfileRepository
    lateinit var validateNicknameUseCase: ValidateNicknameUseCase
    lateinit var viewModel: ProfileUpdateViewModel

    val defaultProfile = Profile("", BirthDateStatus.NotSpecified, ProfileImageStatus.Default)

    context("초기 상태 업데이트 - container onCreate()") {
        profileRepository = mockk()
        validateNicknameUseCase = mockk()
        viewModel = ProfileUpdateViewModel(profileRepository, validateNicknameUseCase)

        Given("유저의 프로필을 불러왔을 때") {
            When("생일이 존재하면") {
                val sampleBirthDateStatus = BirthDateStatus.Specified(LocalDate.of(1999, 4, 29))
                coEvery { profileRepository.getProfile() } returns flowOf(
                    Result.success(defaultProfile.copy(birthDate = sampleBirthDateStatus))
                )
                Then("상태에 유저의 생일을 yyyy.MM.dd 형식으로 설정한다") {
                    runTest {
                        viewModel.test(this) {
                            runOnCreate()

                            expectState {
                                ProfileUpdateState(
                                    birthDateInput = "1999.04.29"
                                )
                            }
                        }
                    }
                }
            }
            When("생일이 존재하지 않으면") {
                val sampleBirthDateStatus = BirthDateStatus.NotSpecified
                coEvery { profileRepository.getProfile() } returns flowOf(
                    Result.success(defaultProfile.copy(birthDate = sampleBirthDateStatus))
                )
                Then("상태에 유저의 생일을 빈 값으로 설정한다") {
                    runTest {
                        viewModel.test(this) {
                            runOnCreate().join()

                            viewModel.container.stateFlow.value shouldBe ProfileUpdateState(
                                birthDateInput = ""
                            )
                            cancelAndIgnoreRemainingItems()
                        }
                    }
                }
            }
            When("프로필 이미지가 존재하면") {
                val sampleImageStatus = ProfileImageStatus.Custom("Sample URL")
                coEvery { profileRepository.getProfile() } returns flowOf(
                    Result.success(defaultProfile.copy(image = sampleImageStatus))
                )
                Then("상태에 프로필 이미지를 Uri 형식으로 설정한다") {
                    runTest {
                        viewModel.test(this) {
                            runOnCreate()

                            expectState {
                                ProfileUpdateState(imageUriInput = sampleImageStatus.url)
                            }
                        }
                    }
                }
            }
            When("프로필 이미지가 기본 프로필 이미지면") {
                val sampleImageStatus = ProfileImageStatus.Default
                coEvery { profileRepository.getProfile() } returns flowOf(
                    Result.success(defaultProfile.copy(image = sampleImageStatus))
                )
                Then("상태에 프로필 이미지를 null로 설정한다") {
                    runTest {
                        viewModel.test(this) {
                            runOnCreate().join()

                            viewModel.container.stateFlow.value shouldBe ProfileUpdateState(
                                imageUriInput = null
                            )
                            cancelAndIgnoreRemainingItems()
                        }
                    }
                }
            }
        }
    }

    context("텍스트 입력 처리") {
        profileRepository = mockk()
        validateNicknameUseCase = mockk()
        viewModel = ProfileUpdateViewModel(profileRepository, validateNicknameUseCase)

        runTest {
            viewModel.test(this) {
                Given("onNicknameInputChanged()는") {
                    When("새로운 입력 값이 발생하면") {

                        val sampleNewInput = "acon123"
                        Then("닉네임 입력 상태에 반영한다") {
                            viewModel.onNicknameInputChanged(sampleNewInput)

                            expectState {
                                ProfileUpdateState(
                                    nicknameInput = sampleNewInput
                                )
                            }
                        }
                    }

                    When("닉네임 유효성 검사를 수행하여") {
                        And("통과하면") {
                            coEvery { validateNicknameUseCase(any()) } returns Result.success(Unit)

                            Then("닉네임 유효성 상태를 '사용 가능'으로 설정한다") {
                                viewModel.onNicknameInputChanged("").join()

                                coVerify(exactly = 1) { validateNicknameUseCase("") }
                                expectState {
                                    ProfileUpdateState(
                                        nicknameValidationStatus = NicknameValidationStatus.Available
                                    )
                                }

                            }
                        }
                        And("빈 입력 에러를 반환받으면") {
                            coEvery { validateNicknameUseCase(any()) } returns Result.failure(
                                ValidateNicknameError.EmptyInput()
                            )

                            Then("닉네임 유효성 상태를 `빈 입력`으로 설정한다") {
                                viewModel.onNicknameInputChanged("").join()

                                coVerify(exactly = 1) { validateNicknameUseCase("") }
                                expectState {
                                    ProfileUpdateState(
                                        nicknameValidationStatus = NicknameValidationStatus.Empty
                                    )
                                }
                            }
                        }
                        And("중복된 닉네임 에러를 반환받으면") {
                            coEvery { validateNicknameUseCase(any()) } returns Result.failure(
                                ValidateNicknameError.AlreadyExist()
                            )

                            Then("닉네임 유효성 상태를 `중복`으로 설정한다") {
                                viewModel.onNicknameInputChanged("").join()

                                coVerify(exactly = 1) { validateNicknameUseCase("") }
                                expectState {
                                    ProfileUpdateState(
                                        nicknameValidationStatus = NicknameValidationStatus.AlreadyExist
                                    )
                                }
                            }
                        }
                        And("잘못된 닉네임 형식 에러를 반환받으면") {
                            coEvery { validateNicknameUseCase(any()) } returns Result.failure(
                                ValidateNicknameError.InvalidFormat()
                            )

                            Then("닉네임 유효성 상태를 `잘못된 형식`으로 설정한다") {
                                viewModel.onNicknameInputChanged("").join()

                                coVerify(exactly = 1) { validateNicknameUseCase("") }
                                expectState {
                                    ProfileUpdateState(
                                        nicknameValidationStatus = NicknameValidationStatus.InvalidFormat
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
})