package com.acon.feature.profile.update.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.acon.acon.core.model.model.profile.BirthDateStatus
import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import com.acon.acon.domain.error.profile.ValidateBirthDateError
import com.acon.acon.domain.error.profile.ValidateNicknameError
import com.acon.acon.domain.repository.ProfileRepository
import com.acon.acon.domain.usecase.ValidateBirthDateUseCase
import com.acon.acon.domain.usecase.ValidateNicknameUseCase
import com.acon.feature.profile.update.status.BirthDateValidationStatus
import com.acon.feature.profile.update.status.NicknameValidationStatus
import com.acon.feature.profile.update.status.ProfileImageInputStatus
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.orbitmvi.orbit.test.test
import java.time.LocalDate

class ProfileUpdateViewModelTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    lateinit var profileRepository: ProfileRepository
    lateinit var validateNicknameUseCase: ValidateNicknameUseCase
    lateinit var validateBirthDateUseCase: ValidateBirthDateUseCase
    lateinit var viewModel: ProfileUpdateViewModel

    fun ProfileUpdateViewModel.getState() = container.stateFlow.value

    context("초기 상태 업데이트 - container onCreate()") {
        profileRepository = mockk()
        validateNicknameUseCase = mockk()
        validateBirthDateUseCase = mockk()
        viewModel = ProfileUpdateViewModel(
            profileRepository,
            validateNicknameUseCase,
            validateBirthDateUseCase
        )

        Given("유저의 프로필 불러오기를") {
            When("성공하면") {
                Then("입력 상태에 닉네임을 반영한다") {
                    val sampleProfile = Profile(
                        nickname = "Sample Nickname",
                        birthDate = BirthDateStatus.NotSpecified,
                        image = ProfileImageStatus.Default
                    )
                    coEvery { profileRepository.getProfile() } returns flowOf(
                        Result.success(
                            sampleProfile
                        )
                    )

                    val expectedNicknameInput = TextFieldValue(sampleProfile.nickname)
                    runTest {
                        viewModel.test(this) {
                            runOnCreate()

                            val state = awaitState()
                            state.nicknameInput shouldBe expectedNicknameInput
                        }
                    }
                }

                And("생년월일이 지정되어 있으면") {
                    val sampleProfile = Profile(
                        nickname = "Sample Nickname",
                        birthDate = BirthDateStatus.Specified(LocalDate.of(1999, 12, 25)),
                        image = ProfileImageStatus.Default
                    )
                    coEvery { profileRepository.getProfile() } returns flowOf(
                        Result.success(
                            sampleProfile
                        )
                    )
                    Then(" 생년월일 입력 상태를 8자리 문자열로 설정한다") {
                        val expectedBirthDateInput = TextFieldValue("19991225")
                        runTest {
                            viewModel.test(this) {
                                runOnCreate()

                                val state = awaitState()
                                state.birthDateInput shouldBe expectedBirthDateInput
                            }
                        }
                    }
                }
                And("생년월일이 지정되어 있지 않으면") {
                    val sampleProfile = Profile(
                        nickname = "Sample Nickname",
                        birthDate = BirthDateStatus.NotSpecified,
                        image = ProfileImageStatus.Default
                    )
                    coEvery { profileRepository.getProfile() } returns flowOf(
                        Result.success(
                            sampleProfile
                        )
                    )
                    Then("생년월일 입력 상태를 빈 문자열로 설정한다") {
                        val expectedBirthDateInput = TextFieldValue("")
                        runTest {
                            viewModel.test(this) {
                                runOnCreate()

                                val state = awaitState()
                                state.birthDateInput shouldBe expectedBirthDateInput
                            }
                        }
                    }
                }

                And("프로필 이미지가 지정되어 있으면") {
                    val sampleProfile = Profile(
                        nickname = "Sample Nickname",
                        birthDate = BirthDateStatus.NotSpecified,
                        image = ProfileImageStatus.Custom("Sample Url")
                    )
                    coEvery { profileRepository.getProfile() } returns flowOf(
                        Result.success(
                            sampleProfile
                        )
                    )
                    val expectedProfileImageUriInput = "Sample Url"
                    Then("프로필 이미지 입력 상태를 uri 형식의 문자열로 설정한다") {
                        runTest {
                            viewModel.test(this) {
                                runOnCreate()

                                val state = awaitState()
                                state.profileImageUriInput shouldBe expectedProfileImageUriInput
                            }
                        }
                    }
                }
                And("프로필 이미지가 기본 이미지면") {
                    val sampleProfile = Profile(
                        nickname = "Sample Nickname",
                        birthDate = BirthDateStatus.NotSpecified,
                        image = ProfileImageStatus.Default
                    )
                    coEvery { profileRepository.getProfile() } returns flowOf(
                        Result.success(
                            sampleProfile
                        )
                    )
                    val expectedProfileImageUriInput = null
                    Then("프로필 이미지 입력 상태를 null로 설정한다") {
                        runTest {
                            viewModel.test(this) {
                                runOnCreate()

                                val state = awaitState()
                                state.profileImageUriInput shouldBe expectedProfileImageUriInput
                            }
                        }
                    }
                }
            }
        }
    }

    Given("State의 isSaveEnabled는") {
        viewModel = ProfileUpdateViewModel(mockk(), mockk(), mockk())
        When("닉네임, 생년월일의 유효성 상태와 프로필 이미지 입력 상태가 주어졌을 때") {
            table(
                headers("테스트 설명", "닉네임 유효성 상태", "생년월일 유효성 상태", "프로필 이미지 입력 상태", "기대 결과"),
                row(
                    "닉네임, 생년월일이 유효하고, 생일 입력이 변한 상태",
                    NicknameValidationStatus.Available,
                    BirthDateValidationStatus.Valid,
                    ProfileImageInputStatus.Changed,
                    true
                ),
                row(
                    "닉네임, 생년월일이 유효하고, 생일 입력이 변하지 않은 상태",
                    NicknameValidationStatus.Available,
                    BirthDateValidationStatus.Valid,
                    ProfileImageInputStatus.NotChanged,
                    true
                ),
                row(
                    "닉네임 초기 상태, 생년월일 유효, 프로필 이미지 변한 상태",
                    NicknameValidationStatus.Idle,
                    BirthDateValidationStatus.Valid,
                    ProfileImageInputStatus.Changed,
                    true
                ),
                row(
                    "생년월일 입력 중 상태",
                    NicknameValidationStatus.Available,
                    BirthDateValidationStatus.Idle,
                    ProfileImageInputStatus.Changed,
                    false
                ),
                row(
                    "모두 초기상태",
                    NicknameValidationStatus.Idle,
                    BirthDateValidationStatus.Valid,
                    ProfileImageInputStatus.NotChanged,
                    false
                ),
                row(
                    "생년월일 유효하지 않음",
                    NicknameValidationStatus.Available,
                    BirthDateValidationStatus.Invalid,
                    ProfileImageInputStatus.Changed,
                    false
                ),
                row(
                    "닉네임 로딩 중",
                    NicknameValidationStatus.Loading,
                    BirthDateValidationStatus.Valid,
                    ProfileImageInputStatus.Changed,
                    false
                ),
                row(
                    "닉네임 형식 오류",
                    NicknameValidationStatus.InvalidFormat,
                    BirthDateValidationStatus.Valid,
                    ProfileImageInputStatus.Changed,
                    false
                ),
                row(
                    "닉네임 중복",
                    NicknameValidationStatus.AlreadyExist,
                    BirthDateValidationStatus.Valid,
                    ProfileImageInputStatus.Changed,
                    false
                ),
                row(
                    "닉네임 비어있음",
                    NicknameValidationStatus.Empty,
                    BirthDateValidationStatus.Valid,
                    ProfileImageInputStatus.Changed,
                    false
                ),
                row(
                    "닉네임 초기 상태, 프로필 이미지 안 변한 상태",
                    NicknameValidationStatus.Idle,
                    BirthDateValidationStatus.Valid,
                    ProfileImageInputStatus.NotChanged,
                    false
                ),
                row(
                    "닉네임, 생년월일 유효하지 않고, 프로필 이미지 변한 상태",
                    NicknameValidationStatus.Empty,
                    BirthDateValidationStatus.Invalid,
                    ProfileImageInputStatus.Changed,
                    false
                ),
                row(
                    "닉네임, 생년월일 유효하지 않고, 프로필 이미지 안 변한 상태",
                    NicknameValidationStatus.Empty,
                    BirthDateValidationStatus.Invalid,
                    ProfileImageInputStatus.NotChanged,
                    false
                )
            ).forAll { testDescription, nicknameStatus, birthDateStatus, imageInputState, expected ->
                Then("$testDescription -> 저장 버튼 활성화 상태는 $expected") {
                    runTest {
                        viewModel.test(
                            this, ProfileUpdateState(
                                nicknameValidationStatus = nicknameStatus,
                                birthDateValidationStatus = birthDateStatus,
                                profileImageInputStatus = imageInputState
                            )
                        ) {
                            viewModel.getState().isSaveEnabled shouldBe expected
                        }
                    }
                }
            }
        }
    }

    context("입력 처리") {
        profileRepository = mockk()
        validateNicknameUseCase = mockk()
        validateBirthDateUseCase = mockk()
        viewModel = ProfileUpdateViewModel(
            profileRepository,
            validateNicknameUseCase,
            validateBirthDateUseCase
        )

        Given("onNicknameInputChanged()는") {
            When("Text Value 변화로 발생한 호출일 경우") {

                val sampleNewTextFieldValue = TextFieldValue("acon123")
                And("닉네임 유효성 검사 수행 전") {
                    Then("프로필 상태에 새 닉네임 입력을 반영한다") {
                        runTest {
                            viewModel.test(
                                this, ProfileUpdateState(
                                    nicknameInput = TextFieldValue("acon12")
                                )
                            ) {
                                viewModel.onNicknameInputChanged(sampleNewTextFieldValue)

                                val state = awaitState()
                                state.nicknameInput shouldBe sampleNewTextFieldValue
                            }
                        }
                    }

                    Then("닉네임 유효성 상태는 '로딩'이 된다") {
                        val sampleTextFieldValue = TextFieldValue("acon123")

                        runTest {
                            viewModel.test(this) {
                                viewModel.onNicknameInputChanged(sampleTextFieldValue)

                                val state = awaitState()

                                state.nicknameValidationStatus shouldBe NicknameValidationStatus.Loading
                            }
                        }
                    }

                    And("입력이 14글자를 초과하면") {
                        val sampleNewTextFieldValue = TextFieldValue("이것은14글자를초과하는닉네임")
                        val expectedProfileUpdateState = viewModel.getState()
                        Then("해당 입력 전체를 무시한다") {
                            runTest {
                                viewModel.test(this) {
                                    viewModel.onNicknameInputChanged(sampleNewTextFieldValue).join()

                                    viewModel.getState() shouldBe expectedProfileUpdateState
                                }
                            }
                        }
                    }
                }

                And("닉네임 유효성 검사를 수행하여") {
                    And("통과하면") {
                        coEvery { validateNicknameUseCase(any()) } returns Result.success(Unit)

                        Then("닉네임 유효성 상태를 '사용 가능'으로 설정한다") {
                            runTest {
                                viewModel.test(this) {
                                    viewModel.onNicknameInputChanged(sampleNewTextFieldValue).join()

                                    val state = viewModel.getState()

                                    coVerify(exactly = 1) {
                                        validateNicknameUseCase(
                                            sampleNewTextFieldValue.text
                                        )
                                    }
                                    state.nicknameValidationStatus shouldBe NicknameValidationStatus.Available

                                    cancelAndIgnoreRemainingItems()
                                }
                            }
                        }
                    }
                    And("빈 입력 에러를 반환받으면") {
                        coEvery { validateNicknameUseCase(any()) } returns Result.failure(
                            ValidateNicknameError.EmptyInput()
                        )

                        Then("닉네임 유효성 상태를 `빈 입력`으로 설정한다") {
                            runTest {
                                viewModel.test(this) {
                                    viewModel.onNicknameInputChanged(sampleNewTextFieldValue).join()

                                    val state = viewModel.getState()

                                    coVerify(exactly = 1) {
                                        validateNicknameUseCase(
                                            sampleNewTextFieldValue.text
                                        )
                                    }
                                    state.nicknameValidationStatus shouldBe NicknameValidationStatus.Empty

                                    cancelAndIgnoreRemainingItems()
                                }
                            }
                        }
                    }
                    And("중복된 닉네임 에러를 반환받으면") {
                        coEvery { validateNicknameUseCase(any()) } returns Result.failure(
                            ValidateNicknameError.AlreadyExist()
                        )

                        Then("닉네임 유효성 상태를 `중복`으로 설정한다") {
                            runTest {
                                viewModel.test(this) {
                                    viewModel.onNicknameInputChanged(sampleNewTextFieldValue).join()

                                    val state = viewModel.getState()

                                    coVerify(exactly = 1) {
                                        validateNicknameUseCase(
                                            sampleNewTextFieldValue.text
                                        )
                                    }
                                    state.nicknameValidationStatus shouldBe NicknameValidationStatus.AlreadyExist

                                    cancelAndIgnoreRemainingItems()
                                }
                            }
                        }
                    }
                    And("잘못된 닉네임 형식 에러를 반환받으면") {
                        coEvery { validateNicknameUseCase(any()) } returns Result.failure(
                            ValidateNicknameError.InvalidFormat()
                        )

                        Then("닉네임 유효성 상태를 `잘못된 형식`으로 설정한다") {
                            runTest {
                                viewModel.test(this) {
                                    viewModel.onNicknameInputChanged(sampleNewTextFieldValue).join()

                                    val state = viewModel.getState()

                                    coVerify(exactly = 1) {
                                        validateNicknameUseCase(
                                            sampleNewTextFieldValue.text
                                        )
                                    }
                                    state.nicknameValidationStatus shouldBe NicknameValidationStatus.InvalidFormat

                                    cancelAndIgnoreRemainingItems()
                                }
                            }

                        }
                    }
                }
            }

            When("Text Selection으로 발생한 호출일 경우") {
                val originalNicknameTextFieldValue = TextFieldValue("acon123")
                val selectedTextRange = TextRange(4, 4)
                val expectedNicknameTextFieldValue =
                    originalNicknameTextFieldValue.copy(selection = selectedTextRange)

                Then("nicknameInput 상태만 업데이트하고 그 외에는 수행하지 않는다") {
                    runTest {
                        viewModel.test(
                            this, ProfileUpdateState(
                                nicknameInput = originalNicknameTextFieldValue
                            )
                        ) {
                            viewModel.onNicknameInputChanged(expectedNicknameTextFieldValue).join()
                            coVerify(exactly = 0) { validateBirthDateUseCase(any()) }

                            expectState {
                                ProfileUpdateState(
                                    nicknameInput = expectedNicknameTextFieldValue
                                )
                            }
                        }
                    }
                }
            }
        }

        Given("onBirthDateInputChanged()는") {
            And("입력에 숫자가 아닌 것이 포함되어 있으면") {
                val sampleNewTextFieldValue = TextFieldValue("2025.")
                val expectedProfileUpdateState = viewModel.getState()
                Then("해당 입력 전체를 무시한다") {
                    runTest {
                        viewModel.test(this) {
                            viewModel.onBirthDateInputChanged(sampleNewTextFieldValue).join()

                            viewModel.getState() shouldBe expectedProfileUpdateState
                        }
                    }
                }
            }
            When("Text Value 변화로 발생한 호출일 경우") {
                Then("생년월일 입력 상태를 업데이트한다") {
                    runTest {
                        viewModel.test(
                            this, ProfileUpdateState(
                                birthDateInput = TextFieldValue("19990429")
                            )
                        ) {
                            val sampleBirthDateInput = TextFieldValue("1999042")
                            viewModel.onBirthDateInputChanged(sampleBirthDateInput)

                            val state = awaitState()

                            state.birthDateInput shouldBe sampleBirthDateInput

                            cancelAndIgnoreRemainingItems()
                        }
                    }
                }
                And("입력이 8자리로 완성되지 않았다면") {
                    Then("생년월일 입력 유효성 상태는 IDLE이다") {
                        runTest {
                            viewModel.test(
                                this, ProfileUpdateState(
                                    birthDateInput = TextFieldValue("1999")
                                )
                            ) {
                                val sampleBirthDateInput = TextFieldValue("19990")
                                viewModel.onBirthDateInputChanged(sampleBirthDateInput).join()

                                val state = viewModel.getState()

                                state.birthDateValidationStatus shouldBe BirthDateValidationStatus.Idle

                                cancelAndIgnoreRemainingItems()
                            }
                        }
                    }
                }
                And("올바른 생년월일로 입력이 완료되면") {
                    coEvery { validateBirthDateUseCase(any()) } returns Result.success(Unit)
                    Then("생년월일 입력 유효성 상태는 '유효함'이다") {
                        runTest {
                            viewModel.test(
                                this, ProfileUpdateState(
                                    birthDateInput = TextFieldValue("1999042")
                                )
                            ) {
                                val sampleBirthDateInput = TextFieldValue("19990429")
                                viewModel.onBirthDateInputChanged(sampleBirthDateInput).join()

                                val state = viewModel.getState()

                                state.birthDateValidationStatus shouldBe BirthDateValidationStatus.Valid

                                cancelAndIgnoreRemainingItems()
                            }
                        }
                    }
                }
                And("올바르지 않은 생년월일로 입력이 완료되면") {
                    Then("생년월일 입력 유효성 상태는 '유효하지 않음'이다") {
                        runTest {
                            viewModel.test(
                                this, ProfileUpdateState(
                                    birthDateInput = TextFieldValue("1999043")
                                )
                            ) {
                                val sampleBirthDateInput = TextFieldValue("19990439")
                                viewModel.onBirthDateInputChanged(sampleBirthDateInput).join()

                                val state = viewModel.getState()

                                state.birthDateValidationStatus shouldBe BirthDateValidationStatus.Invalid

                                cancelAndIgnoreRemainingItems()
                            }
                        }
                    }
                }
                And("이번 입력으로 인해 Text가 비어졌으면") {
                    Then("생년월일 입력 유효성 상태는 '유효함'이다") {
                        runTest {
                            viewModel.test(
                                this, ProfileUpdateState(
                                    birthDateInput = TextFieldValue("1")
                                )
                            ) {
                                val sampleBirthDateInput = TextFieldValue("")
                                viewModel.onBirthDateInputChanged(sampleBirthDateInput).join()

                                val state = viewModel.getState()

                                state.birthDateValidationStatus shouldBe BirthDateValidationStatus.Valid

                                cancelAndIgnoreRemainingItems()
                            }
                        }
                    }
                }
            }
            When("Text Selection으로 발생한 호출일 경우") {
                val originalBirthDateInput = TextFieldValue("19990429")
                val selectedTextRange = TextRange(4, 4)
                val expectedBirthDateInput =
                    originalBirthDateInput.copy(selection = selectedTextRange)

                Then("birthDateInput 상태만 업데이트하고 그 외에는 수행하지 않는다") {
                    runTest {
                        viewModel.test(
                            this, ProfileUpdateState(
                                birthDateInput = originalBirthDateInput
                            )
                        ) {
                            viewModel.onBirthDateInputChanged(expectedBirthDateInput).join()
                            coVerify(exactly = 0) { validateBirthDateUseCase(any()) }

                            val state = viewModel.getState()
                            state shouldBe ProfileUpdateState(birthDateInput = expectedBirthDateInput)

                            cancelAndIgnoreRemainingItems()
                        }
                    }
                }
            }
        }

        Given("onDefaultProfileImageSelected()는") {
            runTest {
                viewModel.test(this) {
                    viewModel.onDefaultProfileImageSelected()

                    Then("선택된 프로필 이미지 uri를 null로 설정한다") {
                        val state = awaitState()

                        state.profileImageUriInput shouldBe null
                    }
                    Then("프로필 이미지 입력 상태를 '변경됨'으로 설정한다") {
                        val state = awaitState()

                        state.profileImageInputStatus shouldBe ProfileImageInputStatus.Changed
                    }
                }
            }
        }

        Given("onProfileImageSelected()는") {
            runTest {
                viewModel.test(this) {
                    val sampleUri = "content://..."
                    viewModel.onProfileImageSelected(sampleUri)

                    Then("선택된 프로필 이미지 uri를 넘겨받은 매개변수로 설정한다") {
                        val state = awaitState()

                        state.profileImageUriInput shouldBe sampleUri
                    }
                    Then("프로필 이미지 입력 상태를 '변경됨'으로 설정한다") {
                        val state = awaitState()

                        state.profileImageInputStatus shouldBe ProfileImageInputStatus.Changed
                    }
                }
            }
        }
    }

    context("프로필 저장") {
        profileRepository = mockk()
        validateNicknameUseCase = mockk()
        validateBirthDateUseCase = mockk()
        viewModel = ProfileUpdateViewModel(
            profileRepository,
            validateNicknameUseCase,
            validateBirthDateUseCase
        )

        Given("onSave()는") {
            When("프로필 저장 API를 호출할 때") {
                val profileSlot = slot<Profile>()
                Then("상태에 저장된 입력 값을 바탕으로 API를 호출한다") {
                    coEvery { profileRepository.updateProfile(capture(profileSlot)) } returns Result.success(
                        Unit
                    )
                    runTest {
                        viewModel.test(this, ProfileUpdateState(
                            nicknameInput = TextFieldValue("acon123"),
                            birthDateInput = TextFieldValue("20021225"),
                            profileImageUriInput = null
                        )) {
                            viewModel.onSave().join()

                            val capturedProfile = profileSlot.captured
                            capturedProfile.nickname shouldBe "acon123"
                            capturedProfile.birthDate shouldBe
                                    BirthDateStatus.Specified(LocalDate.of(2002,12,25))
                            capturedProfile.image shouldBe ProfileImageStatus.Default

                            coVerify(exactly = 1) { profileRepository.updateProfile(any()) }

                            cancelAndIgnoreRemainingItems()
                        }
                    }
                }
                And("성공하면") {
                    coEvery { profileRepository.updateProfile(any()) } returns Result.success(
                        Unit
                    )

                    Then("이전 화면으로 전환 SideEffect를 보낸다") {
                        runTest {
                            viewModel.test(this) {
                                viewModel.onSave()

                                expectSideEffect(ProfileUpdateSideEffect.NavigateBack)
                            }
                        }
                    }
                }
                And("실패하면") {
                    coEvery { profileRepository.updateProfile(any()) } returns Result.failure(
                        Exception("")
                    )
                    Then("저장 실패 메시지 출력 SideEffect를 보낸다") {
                        runTest {
                            viewModel.test(this) {
                                viewModel.onSave()

                                expectSideEffect(ProfileUpdateSideEffect.ShowSaveFailedMessage)
                            }
                        }
                    }
                }
            }
        }
    }

    context("뒤로 가기") {
        profileRepository = mockk()
        validateNicknameUseCase = mockk(relaxed = true)
        validateBirthDateUseCase = mockk(relaxed = true)
        viewModel = ProfileUpdateViewModel(
            profileRepository,
            validateNicknameUseCase,
            validateBirthDateUseCase
        )
        runTest {
            viewModel.test(this) {
                Given("닉네임 입력(Text Value 변화)이 발생한 경우") {
                    val sampleTextFieldValue = TextFieldValue("acon123")
                    When("나중에 뒤로가기 할 때") {
                        Then("모달을 보여줘야 하는 상태로 설정한다") {
                            viewModel.onNicknameInputChanged(sampleTextFieldValue).join()

                            val state = viewModel.getState()

                            state.shouldShowExitModal shouldBe true
                        }
                    }
                }
                Given("생일 입력(Text Value 변화)이 발생한 경우") {
                    val sampleTextFieldValue = TextFieldValue("20")
                    When("나중에 뒤로가기 할 때") {
                        Then("모달을 보여줘야 하는 상태로 설정한다") {
                            viewModel.onBirthDateInputChanged(sampleTextFieldValue).join()

                            val state = viewModel.getState()

                            state.shouldShowExitModal shouldBe true
                        }
                    }
                }
                Given("프로필 이미지 변경이 발생한 경우") {
                    When("나중에 뒤로가기 할 때") {
                        Then("모달을 보여줘야 하는 상태로 설정한다") {
                            viewModel.onDefaultProfileImageSelected().join()

                            viewModel.getState().shouldShowExitModal shouldBe true

                            viewModel.intent {
                                reduce { state.copy(shouldShowExitModal = false) }
                            }.join()
                            awaitState()

                            viewModel.onProfileImageSelected("").join()

                            viewModel.getState().shouldShowExitModal shouldBe true
                        }
                    }
                }

                Given("뒤로가기 이벤트 발생 시") {
                    When("모달을 보여줘야 하는 상태이면") {
                        viewModel.intent {
                            reduce {
                                state.copy(shouldShowExitModal = true)
                            }
                        }.join()
                        awaitState()
                        Then("모달 출력 상태를 true로 설정한다") {
                            viewModel.onBack()

                            expectState {
                                ProfileUpdateState(
                                    showExitModal = true,
                                    shouldShowExitModal = true,
                                )
                            }
                        }
                    }
                    When("모달을 보여줘야 하는 상태가 아니면") {
                        viewModel.intent {
                            reduce {
                                state.copy(shouldShowExitModal = false)
                            }
                        }.join()
                        Then("뒤로가기 SideEffect를 보낸다") {
                            viewModel.onBack()

                            expectSideEffect(ProfileUpdateSideEffect.NavigateBack)
                        }
                    }
                }
            }
        }
    }
})