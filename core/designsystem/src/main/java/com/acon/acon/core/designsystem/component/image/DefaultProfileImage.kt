package com.acon.acon.core.designsystem.component.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.acon.acon.core.designsystem.R

@Composable
fun DefaultProfileImage(
    modifier: Modifier = Modifier
) {
    Image(
        imageVector = ImageVector.vectorResource(R.drawable.ic_default_profile),
        contentDescription = stringResource(R.string.content_description_default_profile_image),
        modifier = modifier
    )
}