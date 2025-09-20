package com.acon.core.data.dto.request

import com.acon.acon.core.model.type.ImageType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPresignedUrlRequest(
    @SerialName("imageType") val imageType: ImageType,
    @SerialName("originalFileName") val fileName: String
)
