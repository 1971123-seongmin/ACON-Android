package com.acon.core.data.api.remote

import com.acon.core.data.dto.request.ReplaceVerifiedAreaRequest
import com.acon.core.data.dto.request.profile.UpdateProfileRequest
import com.acon.core.data.dto.response.area.VerifiedAreaListResponse
import com.acon.core.data.dto.response.profile.ProfileResponse
import com.acon.core.data.dto.response.profile.SavedSpotsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
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

    @GET("/api/v1/verified-areas")
    suspend fun getVerifiedAreas() : VerifiedAreaListResponse

    @POST("/api/v1/verified-areas/replacement")
    suspend fun replaceVerifiedArea(
        @Body request: ReplaceVerifiedAreaRequest
    )

    @DELETE("/api/v1/verified-areas/{verifiedAreaId}")
    suspend fun deleteVerifiedArea(
        @Path("verifiedAreaId") verifiedAreaId: Long
    )
}