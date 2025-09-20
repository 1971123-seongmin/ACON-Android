package com.acon.feature.profile.update.composable

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.acon.acon.core.designsystem.R
import com.acon.acon.core.ui.android.showToast
import com.acon.feature.profile.update.viewmodel.ProfileUpdateSideEffect
import com.acon.feature.profile.update.viewmodel.ProfileUpdateViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ProfileUpdateScreenContainer(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: ProfileUpdateViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val state by viewModel.collectAsState()

    ProfileUpdateScreen(
        state = state,
        actions = ProfileUpdateScreenActions(
            onBack = viewModel::onBack,
            onProfileImageBoxClick = viewModel::onProfileImageClicked,
            onNicknameInputChange = viewModel::onNicknameInputChanged,
            onBirthDateInputChange = viewModel::onBirthDateInputChanged,
            onSaveButtonClick = viewModel::onSave,
            onDismissExitDialog = viewModel::onDismissExitModal,
            onBackConfirm = viewModel::onBackConfirmed,
            onGalleryImageSelect = {
                viewModel.onProfileImageSelected(it.toString())
            },
            onDefaultImageSelect = viewModel::onDefaultProfileImageSelected,
            onDismissImageSelectDialog = viewModel::onDismissImageSelectModal
        ),
        modifier = modifier
    )

    BackHandler {
        viewModel.onBack()
    }

    viewModel.collectSideEffect { sideEffect ->
        when(sideEffect) {
            is ProfileUpdateSideEffect.NavigateBack -> onNavigateBack()
            is ProfileUpdateSideEffect.ShowSaveFailedMessage -> context.showToast(R.string.unknown_error)
        }
    }
}

internal data class ProfileUpdateScreenActions(
    val onBack: () -> Unit = {},
    val onProfileImageBoxClick: () -> Unit = {},
    val onNicknameInputChange: (TextFieldValue) -> Unit = {},
    val onBirthDateInputChange: (TextFieldValue) -> Unit = {},
    val onSaveButtonClick: () -> Unit = {},
    val onDismissExitDialog: () -> Unit = {},
    val onBackConfirm: () -> Unit = {},
    val onGalleryImageSelect: (Uri) -> Unit = {},
    val onDefaultImageSelect: () -> Unit = {},
    val onDismissImageSelectDialog: () -> Unit = {},
)