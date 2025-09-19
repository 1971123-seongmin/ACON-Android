package com.acon.core.data.dto.response.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedSpotsResponse(
    @SerialName("savedSpotList") val savedSpotList: List<SavedSpotResponse>
)
