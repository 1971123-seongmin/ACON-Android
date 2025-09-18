package com.acon.feature.profile.savedspot.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.acon.feature.profile.savedspot.viewmodel.BookmarkUiSideEffect
import com.acon.feature.profile.savedspot.viewmodel.BookmarkViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun BookmarkScreenContainer(
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit = {},
    onNavigateToSpotDetailScreen: (Long) -> Unit = {},
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()

    BookmarkScreen(
        modifier = modifier,
        state = state,
        onSpotClick = viewModel::onSpotClicked,
        onNavigateToBack = viewModel::navigateToBack
    )

    viewModel.collectSideEffect {
        when(it) {
            is BookmarkUiSideEffect.OnNavigateToBack -> { onNavigateToBack() }
            is BookmarkUiSideEffect.OnNavigateToSpotDetailScreen -> { onNavigateToSpotDetailScreen(it.spotId) }
        }
    }
}