package com.acon.feature.profile.info.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.acon.acon.core.designsystem.R
import com.acon.acon.core.designsystem.component.image.DefaultProfileImage
import com.acon.acon.core.designsystem.noRippleClickable
import com.acon.acon.core.designsystem.theme.AconTheme
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import com.acon.feature.profile.TestTags

@Composable
internal fun UserProfileView(
    nickname: String,
    profileImageStatus: ProfileImageStatus,
    onProfileUpdateIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (profileImageStatus) {
            is ProfileImageStatus.Custom -> AsyncImage(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                model = profileImageStatus.url,
                contentDescription = stringResource(R.string.content_description_profile_image),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_default_profile)
            )

            is ProfileImageStatus.Default -> DefaultProfileImage(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        }

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = nickname,
                style = AconTheme.typography.Headline4,
                color = AconTheme.color.White,
                modifier = Modifier
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.edit_profile),
                    style = AconTheme.typography.Body1,
                    color = AconTheme.color.Gray500,
                )

                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
                    contentDescription = stringResource(R.string.content_description_edit_profile),
                    modifier = Modifier
                        .testTag(TestTags.PROFILE_UPDATE_ICON)
                        .padding(start = 4.dp)
                        .noRippleClickable { onProfileUpdateIconClick() }
                )
            }
        }
    }
}

@Composable
internal fun GuestProfileView(
    onRequestSignInTextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DefaultProfileImage(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )

        Row(
            modifier = Modifier
                .padding(start = 16.dp)
                .noRippleClickable {
                    onRequestSignInTextClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.you_need_sign_in),
                style = AconTheme.typography.Headline4,
                color = AconTheme.color.White,
                modifier = Modifier
            )

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right_24),
                contentDescription = stringResource(R.string.content_description_go_sign_in),
                modifier = Modifier.padding(start = 4.dp),
                tint = AconTheme.color.Gray50
            )
        }
    }
}
