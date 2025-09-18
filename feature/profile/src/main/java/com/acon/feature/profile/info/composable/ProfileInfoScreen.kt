package com.acon.feature.profile.info.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.acon.acon.core.designsystem.R
import com.acon.acon.core.designsystem.component.bottombar.AconBottomBar
import com.acon.acon.core.designsystem.component.bottombar.BottomNavType
import com.acon.acon.core.designsystem.component.error.NetworkErrorView
import com.acon.acon.core.designsystem.component.topbar.AconTopBar
import com.acon.acon.core.designsystem.effect.LocalHazeState
import com.acon.acon.core.designsystem.effect.defaultHazeEffect
import com.acon.acon.core.designsystem.theme.AconTheme
import com.acon.feature.profile.TestTags
import com.acon.feature.profile.info.viewmodel.ProfileInfoUiState
import dev.chrisbanes.haze.hazeSource
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun ProfileInfoScreen(
    state: ProfileInfoUiState,
    actions: ProfileInfoScreenActions,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .hazeSource(LocalHazeState.current)
                .testTag(TestTags.VERTICAL_SCROLLABLE_VIEW)
        ) {
            AconTopBar(
                content = {
                    Text(
                        text = stringResource(R.string.profile_topbar),
                        style = AconTheme.typography.Title4,
                        fontWeight = FontWeight.SemiBold,
                        color = AconTheme.color.White
                    )
                },
                trailingIcon = {
                    IconButton(
                        modifier = Modifier.testTag(TestTags.SETTING_ICON),
                        onClick = actions.onSettingIconClick
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_setting),
                            contentDescription = stringResource(R.string.content_description_settings),
                            tint = AconTheme.color.White
                        )
                    }
                },
                modifier = Modifier.padding(vertical = 14.dp)
            )

            when (state) {
                is ProfileInfoUiState.User -> {
                    UserProfileView(
                        modifier = Modifier
                            .testTag(TestTags.USER_PROFILE_VIEW)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 40.dp),
                        nickname = state.profile.nickname,
                        profileImageStatus = state.profile.image,
                        onProfileUpdateIconClick = actions.onProfileUpdateIconClick
                    )

                    SavedSpotsView(
                        savedSpots = state.savedSpots.toImmutableList(),
                        onSeeAllTextClick = actions.onSeeAllTextClick,
                        onSavedSpotItemClick = actions.onSavedSpotItemClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 42.dp)
                    )
                }

                is ProfileInfoUiState.Guest -> {
                    GuestProfileView(
                        onRequestSignInTextClick = actions.onRequestSignInTextClick,
                        modifier = Modifier
                            .testTag(TestTags.GUEST_PROFILE_VIEW)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 40.dp),
                    )
                }

                is ProfileInfoUiState.Loading -> {

                }

                is ProfileInfoUiState.LoadFailed -> {
                    NetworkErrorView(
                        onRetry = actions.retryOnError,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        AconBottomBar(
            selectedItem = BottomNavType.PROFILE,
            onItemClick = { bottomType ->
                when (bottomType) {
                    BottomNavType.SPOT -> actions.onSpotListTabClick()
                    BottomNavType.UPLOAD -> actions.onUploadTabClick()
                    BottomNavType.PROFILE -> Unit
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultHazeEffect(
                    hazeState = LocalHazeState.current,
                    tintColor = AconTheme.color.GlassGray900
                )
                .navigationBarsPadding()
                .testTag(TestTags.BOTTOM_BAR)
        )
    }
}