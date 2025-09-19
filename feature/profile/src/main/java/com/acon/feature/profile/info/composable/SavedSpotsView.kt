package com.acon.feature.profile.info.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.acon.acon.core.designsystem.R
import com.acon.acon.core.designsystem.effect.imageGradientLayer
import com.acon.acon.core.designsystem.effect.imageGradientTopLayer
import com.acon.acon.core.designsystem.image.rememberDefaultLoadImageErrorPainter
import com.acon.acon.core.designsystem.noRippleClickable
import com.acon.acon.core.designsystem.theme.AconTheme
import com.acon.acon.core.model.model.profile.SavedSpot
import com.acon.acon.core.model.model.profile.SpotThumbnailStatus
import com.acon.feature.profile.TestTags
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SavedSpotsView(
    savedSpots: ImmutableList<SavedSpot>,
    onSeeAllTextClick: () -> Unit,
    onSavedSpotItemClick: (spotId: Long) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.saved_store),
                color = AconTheme.color.White,
                style = AconTheme.typography.Title4,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Spacer(Modifier.weight(1f))
            if (savedSpots.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.show_saved_all_store),
                    color = AconTheme.color.Action,
                    style = AconTheme.typography.Body1,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .noRippleClickable { onSeeAllTextClick() }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        if (savedSpots.isNotEmpty()) {
            LazyRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = savedSpots,
                    key = { it.spotId }
                ) { spot ->
                    SavedSpotItem(
                        spot = spot,
                        onClick = { onSavedSpotItemClick(spot.spotId) },
                        modifier = Modifier
                            .aspectRatio(150f / 217f)
                            .testTag(TestTags.SAVED_SPOT_ITEM + spot.spotId)
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.no_saved_spot),
                style = AconTheme.typography.Body1,
                fontWeight = FontWeight.W400,
                color = AconTheme.color.Gray500,
            )
        }
    }
}

@Composable
private fun SavedSpotItem(
    spot: SavedSpot,
    onClick: (spotId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(spot.spotId) }
    ) {
        when (val thumbnailStatus = spot.spotThumbnail) {
            is SpotThumbnailStatus.Exist -> {
                AsyncImage(
                    model = thumbnailStatus.url,
                    contentDescription = stringResource(R.string.store_background_image_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .imageGradientTopLayer(),
                    error = rememberDefaultLoadImageErrorPainter()
                )

                Text(
                    text = if (spot.spotName.length > 9) spot.spotName.take(8) + stringResource(R.string.ellipsis) else spot.spotName,
                    color = AconTheme.color.White,
                    style = AconTheme.typography.Title5,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .padding(horizontal = 20.dp)
                )
            }

            else -> {
                Image(
                    painter = painterResource(R.drawable.ic_bg_no_store_profile),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .imageGradientLayer()
                )

                Text(
                    text = if (spot.spotName.length > 9) spot.spotName.take(8) + stringResource(R.string.ellipsis) else spot.spotName,
                    color = AconTheme.color.White,
                    style = AconTheme.typography.Title5,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                        .padding(horizontal = 20.dp)
                )

                Text(
                    text = stringResource(R.string.no_store_image),
                    color = AconTheme.color.Gray50,
                    style = AconTheme.typography.Caption1,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}