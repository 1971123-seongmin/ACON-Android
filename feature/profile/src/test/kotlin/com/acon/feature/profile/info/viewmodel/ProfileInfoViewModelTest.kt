package com.acon.feature.profile.info.viewmodel

import com.acon.acon.core.model.model.profile.BirthDateStatus
import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import com.acon.acon.core.model.model.profile.SavedSpot
import com.acon.acon.core.model.model.profile.SpotThumbnailStatus
import com.acon.acon.core.model.model.spot.SpotNavigationParameter
import com.acon.acon.core.model.type.SignInStatus
import com.acon.acon.domain.repository.ProfileRepository
import com.acon.acon.domain.repository.UserRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.orbitmvi.orbit.test.test

class ProfileInfoViewModelTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    lateinit var userRepository: UserRepository
    lateinit var profileRepository: ProfileRepository
    lateinit var viewModel: ProfileInfoViewModel

    context("초기 상태 업데이트 - container onCreate()") {
        userRepository = mockk()
        profileRepository = mockk()
        viewModel = ProfileInfoViewModel(userRepository, profileRepository)

        Given("로그인 유저일 경우") {

            coEvery { userRepository.getSignInStatus() } returns flowOf(SignInStatus.USER)

            val sampleProfile = Profile(
                nickname = "Sample Nickname",
                birthDate = BirthDateStatus.NotSpecified,
                image = ProfileImageStatus.Default
            )
            val sampleSavedSpots = listOf(
                SavedSpot(1, "Sample Spot1", SpotThumbnailStatus.Empty),
                SavedSpot(2, "Sample Spot2", SpotThumbnailStatus.Exist("url1")),
                SavedSpot(3, "Sample Spot3", SpotThumbnailStatus.Exist("url2"))
            )

            When("프로필과 저장한 장소를 불러오는데,") {
                And("둘 다 성공하면") {
                    coEvery { profileRepository.getProfile() } returns flowOf(
                        Result.success(sampleProfile)
                    )
                    coEvery { profileRepository.getSavedSpots() } returns flowOf(Result.success(
                        sampleSavedSpots
                    ))

                    Then("상태를 두 모델 값이 반영된 성공 상태로 업데이트한다") {
                        runTest {
                            viewModel.test(this) {
                                runOnCreate().join()

                                coVerify(exactly = 1) { profileRepository.getProfile() }
                                coVerify(exactly = 1) { profileRepository.getSavedSpots() }

                                expectState {
                                    ProfileInfoUiState.User(sampleProfile, sampleSavedSpots)
                                }
                            }
                        }
                    }
                }
                And("프로필 로드만 성공하면") {
                    coEvery { profileRepository.getProfile() } returns flowOf(
                        Result.success(
                            sampleProfile
                        )
                    )
                    coEvery { profileRepository.getSavedSpots() } returns flowOf(Result.failure(mockk()))
                    Then("상태를 실패로 업데이트한다") {
                        runTest {
                            viewModel.test(this) {
                                runOnCreate().join()

                                coVerify(exactly = 1) { profileRepository.getProfile() }
                                coVerify(exactly = 1) { profileRepository.getSavedSpots() }
                                expectState { ProfileInfoUiState.LoadFailed }
                            }
                        }
                    }
                }
                And("저장한 장소 로드만 성공하면") {
                    coEvery { profileRepository.getProfile() } returns flowOf(
                        Result.failure(mockk())
                    )
                    coEvery { profileRepository.getSavedSpots() } returns flowOf(Result.success(
                        sampleSavedSpots
                    ))

                    Then("상태를 실패로 업데이트한다") {
                        runTest {
                            viewModel.test(this) {
                                runOnCreate().join()

                                coVerify(exactly = 1) { profileRepository.getProfile() }
                                coVerify(exactly = 1) { profileRepository.getSavedSpots() }
                                expectState { ProfileInfoUiState.LoadFailed }
                            }
                        }
                    }
                }
            }
        }

        Given("비로그인 유저일 경우") {
            coEvery { userRepository.getSignInStatus() } returns flowOf(SignInStatus.GUEST)

            Then("비로그인 상태로 업데이트한다") {
                runTest {
                    viewModel.test(this) {
                        runOnCreate().join()

                        expectState { ProfileInfoUiState.Guest }
                    }
                }
            }
        }
    }

    context("화면 이동") {
        userRepository = mockk()
        profileRepository = mockk()
        viewModel = ProfileInfoViewModel(userRepository, profileRepository)

        Given("onSpotListClicked()는") {
            When("추천 장소 리스트 UI가 눌렸을 때") {
                Then("추천 장소 리스트 화면 이동 SideEffect를 보낸다") {
                    runTest {
                        viewModel.test(this) {
                            viewModel.onSpotListClicked()

                            expectSideEffect(ProfileInfoSideEffect.NavigateToSpotList)
                        }
                    }
                }
            }
        }

        Given("onUploadClicked()는") {
            When("업로드 UI가 눌렸을 때") {
                And("비로그인일 경우") {
                    coEvery { userRepository.getSignInStatus() } returns flowOf(SignInStatus.GUEST)
                    Then("로그인 요청 함수를 호츨한다") {
                        // TODO
                    }
                }
                And("로그인일 경우") {
                    coEvery { userRepository.getSignInStatus() } returns flowOf(SignInStatus.USER)
                    Then("업로드 화면 이동 SideEffect를 보낸다") {
                        runTest {
                            viewModel.test(this) {
                                viewModel.onUploadClicked()

                                expectSideEffect(ProfileInfoSideEffect.NavigateToUpload)
                            }
                        }
                    }
                }
            }
        }

        Given("onProfileUpdateClicked()는") {
            When("프로필 수정 UI가 눌렸을 때") {
                Then("프로필 수정 화면 이동 SideEffect를 보낸다") {
                    runTest {
                        viewModel.test(this) {
                            viewModel.onProfileUpdateClicked()

                            expectSideEffect(ProfileInfoSideEffect.NavigateToProfileUpdate)
                        }
                    }
                }
            }
        }

        Given("onSavedSpotClicked()는") {
            When("저장한 장소 하나가 눌렸을 때") {
                val sampleSavedSpotId = 123L
                val sampleSpotNavigationParam = SpotNavigationParameter(
                    spotId = sampleSavedSpotId,
                    tags = emptyList(),
                    transportMode = null,
                    eta = null,
                    isFromDeepLink = null,
                    navFromProfile = true
                )
                Then("해당 저장한 장소 디테일 화면 이동 SideEffect를 보낸다") {
                    runTest {
                        viewModel.test(this) {
                            viewModel.onSavedSpotClicked(sampleSavedSpotId)

                            expectSideEffect(
                                ProfileInfoSideEffect
                                    .NavigateToSpotDetail(sampleSpotNavigationParam)
                            )
                        }
                    }
                }
            }
        }

        Given("onSeeAllSavedSpotsClicked()는") {
            When("전체보기 UI가 눌렸을 때") {
                Then("저장한 장소 전체 보기 화면 이동 SideEffect를 보낸다") {
                    runTest {
                        viewModel.test(this) {
                            viewModel.onSeeAllSavedSpotsClicked()

                            expectSideEffect(ProfileInfoSideEffect.NavigateToSavedSpots)
                        }
                    }
                }
            }
        }

        Given("onSettingClicked()는") {
            When("설정 UI가 눌렸을 때") {
                Then("설정 화면 이동 SideEffect를 보낸다") {
                    runTest {
                        viewModel.test(this) {
                            viewModel.onSettingClicked()

                            expectSideEffect(ProfileInfoSideEffect.NavigateToSetting)
                        }
                    }
                }
            }
        }
    }
})