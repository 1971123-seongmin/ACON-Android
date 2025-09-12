package com.acon.feature.profile.update.viewmodel

import com.acon.acon.core.common.utils.toyyyyMMdd
import com.acon.acon.core.model.model.profile.BirthDateStatus
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import com.acon.acon.core.ui.base.BaseContainerHost
import com.acon.acon.domain.error.profile.ValidateNicknameError
import com.acon.acon.domain.repository.ProfileRepository
import com.acon.acon.domain.usecase.ValidateNicknameUseCase
import com.acon.feature.profile.update.status.NicknameValidationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileUpdateViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val validateNicknameUseCase: ValidateNicknameUseCase
) : BaseContainerHost<ProfileUpdateState, ProfileUpdateSideEffect>() {

    override val container: Container<ProfileUpdateState, ProfileUpdateSideEffect> =
        container(ProfileUpdateState()) {
            profileRepository.getProfile().collect { profileResult ->
                profileResult.onSuccess { profile ->
                    reduce {
                        ProfileUpdateState(
                            nicknameInput = profile.nickname,
                            birthDateInput = when(val birthDateStatus = profile.birthDate) {
                                is BirthDateStatus.Specified -> birthDateStatus.date.toyyyyMMdd()
                                is BirthDateStatus.NotSpecified -> ""
                            },
                            imageUriInput = when(val imageStatus = profile.image) {
                                is ProfileImageStatus.Custom -> imageStatus.url
                                is ProfileImageStatus.Default -> null
                            }
                        )
                    }
                }
            }
        }


    fun onNicknameInputChanged(input: String) = intent {
        reduce {
            state.copy(nicknameInput = input)
        }
        validateNicknameUseCase(input).onSuccess {
            reduce {
                state.copy(nicknameValidationStatus = NicknameValidationStatus.Available)
            }
        }.onFailure { e ->
            reduce {
                state.copy(
                    nicknameValidationStatus =
                        when (e) {
                            is ValidateNicknameError.EmptyInput -> NicknameValidationStatus.Empty
                            is ValidateNicknameError.AlreadyExist -> NicknameValidationStatus.AlreadyExist
                            is ValidateNicknameError.InvalidFormat -> NicknameValidationStatus.InvalidFormat
                            else -> NicknameValidationStatus.Idle
                        }
                )
            }
        }
    }
}

data class ProfileUpdateState(
    val nicknameInput: String = "",
    val birthDateInput: String = "",
    val imageUriInput: String? = null,
    val nicknameValidationStatus: NicknameValidationStatus = NicknameValidationStatus.Idle,
    val isSaveEnabled: Boolean = false
)

sealed interface ProfileUpdateSideEffect {

}