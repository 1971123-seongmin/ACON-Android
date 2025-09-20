package com.acon.feature.profile.update.composable

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import coil3.compose.AsyncImage
import com.acon.acon.core.designsystem.R
import com.acon.acon.core.designsystem.component.image.DefaultProfileImage
import com.acon.acon.core.designsystem.noRippleClickable

@Composable
internal fun UpdatableProfileImageView(
    imageUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.noRippleClickable {
            onClick()
        }
    ) {
        when {
            imageUri != null -> AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                model = imageUri,
                contentDescription = stringResource(R.string.content_description_profile_image),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_default_profile)
            )
            else -> DefaultProfileImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_profile_img_edit),
            contentDescription = stringResource(R.string.content_description_edit_profile),
            modifier = Modifier.align(alignment = Alignment.BottomEnd)
        )
    }
}