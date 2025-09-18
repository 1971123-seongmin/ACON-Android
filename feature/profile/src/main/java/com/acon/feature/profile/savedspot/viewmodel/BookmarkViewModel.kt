package com.acon.feature.profile.savedspot.viewmodel

import androidx.compose.runtime.Immutable
import com.acon.acon.core.model.model.profile.SavedSpot
import com.acon.acon.core.ui.base.BaseContainerHost
import com.acon.acon.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(OrbitExperimental::class)
@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : BaseContainerHost<BookmarkUiState, BookmarkUiSideEffect>() {

    override val container =  container<BookmarkUiState, BookmarkUiSideEffect>(BookmarkUiState.Loading) {
        delay(LOADING_DELAY_MILLIS)
        profileRepository.getSavedSpots().collect { result ->
            result.onSuccess {
                reduce {
                    BookmarkUiState.Success(savedSpots = it)
                }
            }.onFailure {
                reduce {
                    BookmarkUiState.LoadFailed
                }
            }
        }
    }

    fun navigateToBack() = intent {
        postSideEffect(BookmarkUiSideEffect.OnNavigateToBack)
    }

    fun onSpotClicked(spotId: Long) = intent {
        runOn<BookmarkUiState.Success> {
            postSideEffect(BookmarkUiSideEffect.OnNavigateToSpotDetailScreen(spotId))
        }
    }

    companion object {
        private const val LOADING_DELAY_MILLIS = 800L
    }
}

sealed interface BookmarkUiState {
    @Immutable
    data class Success(
        val savedSpots: List<SavedSpot>
    ) : BookmarkUiState
    data object Loading : BookmarkUiState
    data object LoadFailed : BookmarkUiState
}

sealed interface BookmarkUiSideEffect {
    data class OnNavigateToSpotDetailScreen(val spotId: Long) : BookmarkUiSideEffect
    data object OnNavigateToBack : BookmarkUiSideEffect
}