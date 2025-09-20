package com.acon.core.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlResponse(
    @SerialName("fileUrl") val fileUrl: String,
    @SerialName("preSignedUrl") val presignedUrl: String
)
