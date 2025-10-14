package com.acon.feature.profile.info.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.acon.acon.core.model.model.spot.SpotNavigationParameter
import com.acon.feature.profile.info.viewmodel.ProfileInfoSideEffect
import com.acon.feature.profile.info.viewmodel.ProfileInfoViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ProfileInfoScreenContainer(
    onNavigateToProfileUpdate: () -> Unit,
    onNavigateToSpotDetail: (SpotNavigationParameter) -> Unit,
    onNavigateToSavedSpots: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToSpotList: () -> Unit,
    onNavigateToUpload: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileInfoViewModel = hiltViewModel()
) {

    val state by viewModel.collectAsState()
    viewModel.initOnRequestSignIn()

    ProfileInfoScreen(
        state = state,
        modifier = modifier,
        actions = ProfileInfoScreenActions(
            onSavedSpotItemClick = viewModel::onSavedSpotClicked,
            onProfileUpdateIconClick = viewModel::onProfileUpdateClicked,
            onSettingIconClick = viewModel::onSettingClicked,
            onSeeAllTextClick = viewModel::onSeeAllSavedSpotsClicked,
            onRequestSignInTextClick = viewModel::onRequestSignIn,
            onSpotListTabClick = viewModel::onSpotListClicked,
            onUploadTabClick = viewModel::onUploadClicked,
            retryOnError = {
                viewModel.intent {
                    with(viewModel) {
                        loadState()
                    }
                }
            }
        )
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileInfoSideEffect.NavigateToProfileUpdate -> onNavigateToProfileUpdate()
            is ProfileInfoSideEffect.NavigateToSpotDetail -> onNavigateToSpotDetail(sideEffect.spotNavigationParam)
            is ProfileInfoSideEffect.NavigateToSavedSpots -> onNavigateToSavedSpots()
            is ProfileInfoSideEffect.NavigateToSetting -> onNavigateToSetting()
            is ProfileInfoSideEffect.NavigateToSpotList -> onNavigateToSpotList()
            is ProfileInfoSideEffect.NavigateToUpload -> onNavigateToUpload()
        }
    }
}

internal data class ProfileInfoScreenActions(
    val onSavedSpotItemClick: (spotId: Long) -> Unit,
    val onProfileUpdateIconClick: () -> Unit,
    val onSettingIconClick: () -> Unit,
    val onSeeAllTextClick: () -> Unit,
    val onRequestSignInTextClick: () -> Unit,
    val onSpotListTabClick: () -> Unit,
    val onUploadTabClick: () -> Unit,
    val retryOnError: () -> Unit,
) {
    companion object {
        val Default = ProfileInfoScreenActions(
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}