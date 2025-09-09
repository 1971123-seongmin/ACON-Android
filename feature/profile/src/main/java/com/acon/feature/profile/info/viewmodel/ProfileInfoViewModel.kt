package com.acon.feature.profile.info.viewmodel

import androidx.lifecycle.viewModelScope
import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.SavedSpot
import com.acon.acon.core.model.model.spot.SpotNavigationParameter
import com.acon.acon.core.model.type.SignInStatus
import com.acon.acon.core.ui.base.BaseContainerHost
import com.acon.acon.domain.repository.ProfileRepository
import com.acon.acon.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) : BaseContainerHost<ProfileInfoUiState, ProfileInfoSideEffect>() {

    override val container: Container<ProfileInfoUiState, ProfileInfoSideEffect> =
        container(ProfileInfoUiState.Loading) {
            userRepository.getSignInStatus().collectLatest { userType ->
                when (userType) {
                    SignInStatus.USER -> {
                        val savedSpotsResultDeferred = viewModelScope.async {
                            profileRepository.getSavedSpots()
                        }
                        profileRepository.getProfile().collect { profileResult: Result<Profile> ->
                            val savedSpotsResult = savedSpotsResultDeferred.await()
                            when {
                                savedSpotsResult.isFailure || profileResult.isFailure -> {
                                    reduce {
                                        ProfileInfoUiState.LoadFailed
                                    }
                                }
                                else -> {
                                    val savedSpots = savedSpotsResult.getOrNull()!!
                                    val profile = profileResult.getOrNull()!!
                                    reduce {
                                        ProfileInfoUiState.User(
                                            profile = profile,
                                            savedSpots = savedSpots
                                        )
                                    }
                                }
                            }
                        }
                    }
                    SignInStatus.GUEST -> {
                        reduce {
                            ProfileInfoUiState.Guest
                        }
                    }
                }
            }
        }

    fun onProfileUpdateClicked() = intent {
        postSideEffect(ProfileInfoSideEffect.NavigateToProfileUpdate)
    }

    fun onSavedSpotClicked(spotId: Long) = intent {
        postSideEffect(ProfileInfoSideEffect.NavigateToSpotDetail(
            SpotNavigationParameter(
                spotId = spotId,
                tags = emptyList(),
                transportMode = null,
                eta = null,
                isFromDeepLink = null,
                navFromProfile = true
            )
        ))
    }

    fun onSeeAllSavedSpotsClicked() = intent {
        postSideEffect(ProfileInfoSideEffect.NavigateToSavedSpots)
    }

    fun onSettingClicked() = intent {
        postSideEffect(ProfileInfoSideEffect.NavigateToSetting)
    }
}

sealed interface ProfileInfoUiState {

    data object Guest : ProfileInfoUiState
    data class User(
        val profile: Profile,
        val savedSpots: List<SavedSpot>
    ) : ProfileInfoUiState

    data object Loading : ProfileInfoUiState
    data object LoadFailed : ProfileInfoUiState
}

sealed interface ProfileInfoSideEffect {
    data object NavigateToProfileUpdate : ProfileInfoSideEffect
    data class NavigateToSpotDetail(val spotNavigationParam: SpotNavigationParameter) : ProfileInfoSideEffect
    data object NavigateToSavedSpots : ProfileInfoSideEffect
    data object NavigateToSetting : ProfileInfoSideEffect
}