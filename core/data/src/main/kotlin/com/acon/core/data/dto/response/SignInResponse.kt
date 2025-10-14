package com.acon.core.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(
    @SerialName("externalUUID") val externalUUID: String,
    @SerialName("accessToken") val accessToken: String?,
    @SerialName("refreshToken") val refreshToken: String?,
    @SerialName("hasVerifiedArea") val hasVerifiedArea: Boolean,
    @SerialName("hasPreference") val hasPreference: Boolean
)