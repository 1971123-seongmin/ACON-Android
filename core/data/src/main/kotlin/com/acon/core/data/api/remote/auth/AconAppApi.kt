package com.acon.core.data.api.remote.auth

import com.acon.core.data.dto.request.GetPresignedUrlRequest
import com.acon.core.data.dto.response.PresignedUrlResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AconAppApi {

    @POST("/api/v1/images/presigned-url")
    suspend fun getPresignedUrl(
        @Body request: GetPresignedUrlRequest
    ): PresignedUrlResponse
}