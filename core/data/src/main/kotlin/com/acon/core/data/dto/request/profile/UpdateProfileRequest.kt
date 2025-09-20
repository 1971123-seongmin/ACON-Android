package com.acon.core.data.dto.request.profile

import com.acon.acon.core.model.model.profile.BirthDateStatus
import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    @SerialName("nickname") val nickname: String,
    @SerialName("birthDate") val birthDate: String?,
    @SerialName("profileImage") val image: String?
)

fun Profile.toUpdateProfileRequest() : UpdateProfileRequest {
    val requestNickname = nickname
    val requestBirthDate: String? = when(birthDate) {
        is BirthDateStatus.Specified -> with((birthDate as BirthDateStatus.Specified).date) {
            "%04d.%02d.%02d".format(year, monthValue, dayOfMonth)
        }
        BirthDateStatus.NotSpecified -> null
    }
    val requestImage: String? = when(image) {
        is ProfileImageStatus.Custom -> (image as ProfileImageStatus.Custom).url
        else -> null
    }

    return UpdateProfileRequest(requestNickname, requestBirthDate, requestImage)
}