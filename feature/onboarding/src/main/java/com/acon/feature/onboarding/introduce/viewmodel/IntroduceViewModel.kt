package com.acon.feature.onboarding.introduce.viewmodel

import androidx.compose.runtime.Immutable
import com.acon.acon.core.model.type.SignInStatus
import com.acon.acon.core.ui.base.BaseContainerHost
import com.acon.acon.domain.repository.OnboardingRepository
import com.acon.acon.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class IntroduceViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val userRepository: UserRepository
) : BaseContainerHost<IntroduceState, IntroduceSideEffect>() {

    override val container = container<IntroduceState, IntroduceSideEffect>(
        initialState = IntroduceState()
    ) {
        onboardingRepository.updateShouldShowIntroduce(false)
    }

    fun onIntroduceLocalReviewScreenDisposed() = intent {
        reduce {
            state.copy(
                shouldShowLocalReviewScreenAnimation = false
            )
        }
    }

    fun onIntroduceTop50ScreenDisposed() = intent {
        reduce {
            state.copy(
                shouldShowTop50ScreenAnimation = false
            )
        }
    }

    fun onIntroduceMainScreenDisposed() = intent {
        reduce {
            state.copy(
                shouldShowMainScreenAnimation = false
            )
        }
    }

    fun onStartButtonClicked() = intent {
        userRepository.getSignInStatus().collect { signInStatus ->
            if(signInStatus == SignInStatus.USER) {
                onboardingRepository.getOnboardingPreferences().onSuccess { pref ->
                    if (pref.shouldVerifyArea)
                        postSideEffect(IntroduceSideEffect.NavigateToAreaVerification)
                    else if (pref.shouldChooseDislikes)
                        postSideEffect(IntroduceSideEffect.NavigateToChooseDislikes)
                    else
                        postSideEffect(IntroduceSideEffect.NavigateToHomeScreen)
                }.onFailure {
                    postSideEffect(IntroduceSideEffect.NavigateToHomeScreen)
                }
            } else {
                postSideEffect(IntroduceSideEffect.NavigateToHomeScreen)
            }
        }
    }
}

@Immutable
data class IntroduceState(
    val shouldShowLocalReviewScreenAnimation: Boolean = true,
    val shouldShowTop50ScreenAnimation: Boolean = true,
    val shouldShowMainScreenAnimation: Boolean = true
)

sealed interface IntroduceSideEffect {
    data object NavigateToAreaVerification: IntroduceSideEffect
    data object NavigateToChooseDislikes : IntroduceSideEffect
    data object NavigateToHomeScreen : IntroduceSideEffect
}