package com.acon.core.data.dto.response.profile

import com.acon.acon.core.model.model.profile.SavedSpot
import com.acon.acon.core.model.model.profile.SpotThumbnailStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedSpotResponse(
    @SerialName("spotId") val spotId: Long,
    @SerialName("name") val spotName: String,
    @SerialName("image") val spotThumbnail: String?
) {

    fun toSavedSpot() : SavedSpot {
        val spotThumbnailStatus = when {
            spotThumbnail.isNullOrBlank() -> SpotThumbnailStatus.Empty
            else -> SpotThumbnailStatus.Exist(spotThumbnail)
        }

        return SavedSpot(spotId, spotName, spotThumbnailStatus)
    }
}
