package com.acon.core.data.dto.response.profile

import com.acon.acon.core.model.model.profile.BirthDateStatus
import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ProfileResponse(
    @SerialName("nickname") val nickname: String,
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("profileImage") val image: String? = null,
) {

    fun toProfile() : Profile {
        val nicknameOfModel = nickname
        val birthDateOfModel = birthDate?.let { dateString ->
            try {
                val (year, month, day) = dateString.split(".").map { it.toInt() }
                BirthDateStatus.Specified(LocalDate.of(year, month, day))
            } catch (_: Exception) {
                BirthDateStatus.NotSpecified
            }
        } ?: BirthDateStatus.NotSpecified
        val imageOfModel =
            if (image == null) ProfileImageStatus.Default else ProfileImageStatus.Custom(image)

        return Profile(nicknameOfModel, birthDateOfModel, imageOfModel)
    }
}
