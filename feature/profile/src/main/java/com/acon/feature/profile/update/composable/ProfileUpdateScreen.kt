package com.acon.feature.profile.update.composable

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.core.net.toUri
import com.acon.acon.core.designsystem.R
import com.acon.acon.core.designsystem.component.bottomsheet.AconBottomSheet
import com.acon.acon.core.designsystem.component.button.v2.AconFilledTextButton
import com.acon.acon.core.designsystem.component.dialog.v2.AconTwoActionDialog
import com.acon.acon.core.designsystem.component.topbar.AconTopBar
import com.acon.acon.core.designsystem.theme.AconTheme
import com.acon.feature.profile.update.viewmodel.ProfileUpdateState

private data class ImageSelectDialogItem(
    @StringRes val titleResId: Int,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileUpdateScreen(
    state: ProfileUpdateState,
    actions: ProfileUpdateScreenActions,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            actions.onGalleryImageSelect(uri)
        }
    }

    if (state.showExitModal) {
        AconTwoActionDialog(
            title = stringResource(R.string.profile_mod_exit_title),
            action1 = stringResource(R.string.continue_writing),
            action2 = stringResource(R.string.exit),
            onDismissRequest = actions.onDismissExitDialog,
            onAction1 = actions.onDismissExitDialog,
            onAction2 = actions.onBackConfirm,
        )
    }

    if (state.showImageSelectModal) {
        AconBottomSheet(
            onDismissRequest = actions.onDismissImageSelectDialog,
            useGlassMorphism = false
        ) {
            remember {
                listOf(
                    ImageSelectDialogItem(R.string.upload_photo_from_album) {
                        actions.onDismissImageSelectDialog()
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    ImageSelectDialogItem(R.string.set_default_profile_image) {
                        actions.onDismissImageSelectDialog()
                        actions.onDefaultImageSelect()
                    },
                )
            }.fastForEach {
                Text(
                    text = stringResource(it.titleResId),
                    color = AconTheme.color.White,
                    style = AconTheme.typography.Title4,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            it.onClick()
                        }
                        .padding(horizontal = 16.dp, vertical = 17.dp)
                )
            }
            Spacer(modifier = Modifier.height(75.dp))
        }
    }

    Column(
        modifier = modifier
    ) {
        AconTopBar(
            leadingIcon = {
                IconButton(
                    onClick = actions.onBack
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_topbar_arrow_left),
                        contentDescription = stringResource(R.string.back),
                        tint = AconTheme.color.Gray50
                    )
                }
            },
            content = {
                Text(
                    text = stringResource(R.string.profile_edit_topbar),
                    style = AconTheme.typography.Title4,
                    fontWeight = FontWeight.SemiBold,
                    color = AconTheme.color.White
                )
            },
            modifier = Modifier.padding(vertical = 14.dp)
        )

        UpdatableProfileImageView(
            imageUri = state.profileImageUriInput?.toUri(),
            onClick = actions.onProfileImageBoxClick,
            modifier = Modifier.size(80.dp).align(Alignment.CenterHorizontally)
        )

        NicknameFieldView(
            input = state.nicknameInput,
            onInputChange = actions.onNicknameInputChange,
            validationStatus = state.nicknameValidationStatus,
            modifier = Modifier
                .padding(top = 48.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )

        BirthDateFieldView(
            input = state.birthDateInput,
            onInputChange = actions.onBirthDateInputChange,
            validationStatus = state.birthDateValidationStatus,
            modifier = Modifier
                .padding(top = 24.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        AconFilledTextButton(
            text = stringResource(R.string.save),
            onClick = actions.onSaveButtonClick,
            enabled = state.isSaveEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .padding(horizontal = 16.dp)
        )
    }
}
