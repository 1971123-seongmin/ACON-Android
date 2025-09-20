package com.acon.core.data.api.remote.noauth

import com.acon.core.data.dto.request.GetPresignedUrlRequest
import com.acon.core.data.dto.response.PresignedUrlResponse
import com.acon.core.data.dto.response.app.ShouldUpdateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AconAppNoAuthApi {
    @GET("/api/v1/app-updates")
    suspend fun fetchShouldUpdateApp(
        @Query("version") currentVersion: String,
        @Query("platform") platform: String = "android"
    ): ShouldUpdateResponse

    @POST("/api/v1/images/presigned-url")
    suspend fun getPresignedUrl(
        @Body request: GetPresignedUrlRequest
    ): PresignedUrlResponse
}