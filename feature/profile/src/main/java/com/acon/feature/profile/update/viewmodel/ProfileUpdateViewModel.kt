package com.acon.feature.profile.update.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.text.isDigitsOnly
import com.acon.acon.core.common.utils.toLocalDate
import com.acon.acon.core.common.utils.toyyyyMMdd
import com.acon.acon.core.model.model.profile.BirthDateStatus
import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import com.acon.acon.core.ui.base.BaseContainerHost
import com.acon.acon.domain.error.profile.ValidateNicknameError
import com.acon.acon.domain.repository.ProfileRepository
import com.acon.acon.domain.usecase.ValidateBirthDateUseCase
import com.acon.acon.domain.usecase.ValidateNicknameUseCase
import com.acon.acon.domain.usecase.ValidateNicknameUseCase.Companion.MAX_NICKNAME_LENGTH
import com.acon.feature.profile.update.status.BirthDateValidationStatus
import com.acon.feature.profile.update.status.NicknameValidationStatus
import com.acon.feature.profile.update.status.ProfileImageInputStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileUpdateViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val validateNicknameUseCase: ValidateNicknameUseCase,
    private val validateBirthDateUseCase: ValidateBirthDateUseCase
) : BaseContainerHost<ProfileUpdateState, ProfileUpdateSideEffect>() {

    private var nicknameValidationJob: Job? = null

    override val container: Container<ProfileUpdateState, ProfileUpdateSideEffect> =
        container(ProfileUpdateState()) {
            profileRepository.getProfile().collect { profileResult ->
                profileResult.onSuccess { profile ->
                    reduce {
                        ProfileUpdateState(
                            nicknameInput = TextFieldValue(profile.nickname),
                            birthDateInput = TextFieldValue(
                                when (val birthDateStatus = profile.birthDate) {
                                    is BirthDateStatus.Specified -> birthDateStatus.date.toyyyyMMdd()
                                    is BirthDateStatus.NotSpecified -> ""
                                }
                            ),
                            profileImageUriInput = when(val imageStatus = profile.image) {
                                is ProfileImageStatus.Custom -> imageStatus.url
                                is ProfileImageStatus.Default -> null
                            }
                        )
                    }
                }
            }
        }

    fun onNicknameInputChanged(input: TextFieldValue): Job? {
        if (input.text.length > MAX_NICKNAME_LENGTH)
            return null

        nicknameValidationJob?.cancel()
        nicknameValidationJob = intent {

            val isJustSelection = input.text == state.nicknameInput.text
            if (isJustSelection) {
                reduce { state.copy(nicknameInput = input) }
                return@intent
            }

            reduce {
                state.copy(
                    nicknameInput = input,
                    shouldShowExitModal = true,
                    nicknameValidationStatus = NicknameValidationStatus.Loading,
                )
            }

            delay(DEBOUNCE_MILLIS)
            validateNicknameUseCase(input.text)
                .onSuccess {
                    reduce { state.copy(nicknameValidationStatus = NicknameValidationStatus.Available) }
                }
                .onFailure { e ->
                    reduce {
                        state.copy(
                            nicknameValidationStatus = when (e) {
                                is ValidateNicknameError.EmptyInput -> NicknameValidationStatus.Empty
                                is ValidateNicknameError.AlreadyExist -> NicknameValidationStatus.AlreadyExist
                                is ValidateNicknameError.InvalidFormat -> NicknameValidationStatus.InvalidFormat
                                else -> NicknameValidationStatus.Idle
                            }
                        )
                    }
                }
        }
        return nicknameValidationJob!!
    }

    fun onBirthDateInputChanged(input: TextFieldValue) = intent {
        if (input.text.length > 8) return@intent
        if (input.text.any { it.isDigit().not() }) return@intent

        val isJustSelection = input.text == state.birthDateInput.text

        if (isJustSelection) {
            reduce {
                state.copy(birthDateInput = input)
            }
        }
        else {
            reduce {
                state.copy(
                    birthDateInput = input,
                    shouldShowExitModal = true,
                )
            }
            if (input.text.isEmpty()) {
                reduce {
                    state.copy(birthDateValidationStatus = BirthDateValidationStatus.Valid)
                }
            } else if (input.text.length in 1 until 8) {
                reduce {
                    state.copy(birthDateValidationStatus = BirthDateValidationStatus.Typing)
                }
            } else if (input.text.length == 8) {
                val localDate = input.text.toLocalDate()
                if (localDate == null)
                    reduce {
                        state.copy(birthDateValidationStatus = BirthDateValidationStatus.Invalid)
                    }
                else {
                    validateBirthDateUseCase(localDate).onSuccess {
                        reduce {
                            state.copy(birthDateValidationStatus = BirthDateValidationStatus.Valid)
                        }
                    }.onFailure {
                        reduce {
                            state.copy(birthDateValidationStatus = BirthDateValidationStatus.Invalid)
                        }
                    }
                }
            }
        }
    }

    fun onDefaultProfileImageSelected() = intent {
        reduce {
            state.copy(
                profileImageUriInput = null,
                profileImageInputStatus = ProfileImageInputStatus.Changed,
                shouldShowExitModal = true
            )
        }
    }

    fun onProfileImageSelected(imageUri: String) = intent {
        reduce {
            state.copy(
                profileImageUriInput = imageUri,
                profileImageInputStatus = ProfileImageInputStatus.Changed,
                shouldShowExitModal = true,
            )
        }
    }

    fun onBack() = intent {
        if (state.shouldShowExitModal) {
            reduce {
                state.copy(showExitModal = true)
            }
        } else {
            postSideEffect(ProfileUpdateSideEffect.NavigateBack)
        }
    }

    fun onProfileImageClicked() = intent {
        reduce {
            state.copy(showImageSelectModal = true)
        }
    }

    fun onDismissImageSelectModal() = intent {
        reduce {
            state.copy(showImageSelectModal = false)
        }
    }

    fun onDismissExitModal() = intent {
        reduce {
            state.copy(showExitModal = false)
        }
    }

    fun onBackConfirmed() = intent {
        reduce {
            state.copy(showExitModal = false)
        }
        postSideEffect(ProfileUpdateSideEffect.NavigateBack)
    }

    fun onSave() = intent {
        profileRepository.updateProfile(Profile(
            nickname = state.nicknameInput.text,
            birthDate = state.birthDateInput.text.toLocalDate()?.let { date ->
                    BirthDateStatus.Specified(date)
                } ?: BirthDateStatus.NotSpecified,
            image = state.profileImageUriInput?.let { ProfileImageStatus.Custom(it) } ?: ProfileImageStatus.Default
        )).onSuccess {
            postSideEffect(ProfileUpdateSideEffect.NavigateBack)
        }.onFailure { _ ->
            postSideEffect(ProfileUpdateSideEffect.ShowSaveFailedMessage)
        }
    }

    companion object {
        private const val DEBOUNCE_MILLIS = 200L
    }
}

@Immutable
data class ProfileUpdateState(
    val nicknameInput: TextFieldValue = TextFieldValue(""),
    val birthDateInput: TextFieldValue = TextFieldValue(""),
    val profileImageUriInput: String? = null,
    val nicknameValidationStatus: NicknameValidationStatus = NicknameValidationStatus.Idle,
    val birthDateValidationStatus: BirthDateValidationStatus = BirthDateValidationStatus.Idle,
    val profileImageInputStatus: ProfileImageInputStatus = ProfileImageInputStatus.NotChanged,
    val showImageSelectModal: Boolean = false,
    val showExitModal: Boolean = false,
    val shouldShowExitModal: Boolean = false,
) {
    val isSaveEnabled: Boolean
        get() = (profileImageInputStatus is ProfileImageInputStatus.NotChanged && (
                        (nicknameValidationStatus is NicknameValidationStatus.Available && (birthDateValidationStatus is BirthDateValidationStatus.Idle || birthDateValidationStatus is BirthDateValidationStatus.Valid)) ||
                                (nicknameValidationStatus is NicknameValidationStatus.Idle && (birthDateValidationStatus is BirthDateValidationStatus.Valid))) ||
                        (profileImageInputStatus is ProfileImageInputStatus.Changed && (nicknameValidationStatus is NicknameValidationStatus.Available || nicknameValidationStatus is NicknameValidationStatus.Idle) && (birthDateValidationStatus is BirthDateValidationStatus.Valid || birthDateValidationStatus is BirthDateValidationStatus.Idle))
                )
}


sealed interface ProfileUpdateSideEffect {
    data object NavigateBack: ProfileUpdateSideEffect
    data object ShowSaveFailedMessage: ProfileUpdateSideEffect
}