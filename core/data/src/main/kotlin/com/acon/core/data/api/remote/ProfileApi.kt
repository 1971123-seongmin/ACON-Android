package com.acon.core.data.api.remote

import com.acon.core.data.dto.request.profile.UpdateProfileRequest
import com.acon.core.data.dto.response.profile.ProfileResponse
import com.acon.core.data.dto.response.profile.SavedSpotResponse
import com.acon.core.data.dto.response.profile.SavedSpotsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface ProfileApi {

    @GET("/api/v1/members/me")
    suspend fun getProfile() : ProfileResponse

    @PATCH("/api/v1/members/me")
    suspend fun updateProfile(@Body updateProfileRequest: UpdateProfileRequest)

    @GET("/api/v1/nickname/validate")
    suspend fun validateNickname(@Query("nickname") nickname: String)

    @GET("/api/v1/saved-spots")
    suspend fun getSavedSpots() : SavedSpotsResponse
}