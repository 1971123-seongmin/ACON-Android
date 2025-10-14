package com.acon.core.data.datasource.remote

import com.acon.core.data.api.remote.auth.AconAppApi
import com.acon.core.data.dto.response.app.ShouldUpdateResponse
import com.acon.core.data.api.remote.noauth.AconAppNoAuthApi
import com.acon.core.data.api.remote.noauth.FileUploadApi
import com.acon.core.data.dto.request.GetPresignedUrlRequest
import com.acon.core.data.dto.response.PresignedUrlResponse
import okhttp3.RequestBody
import javax.inject.Inject

class AconAppRemoteDataSource @Inject constructor(
    private val aconAppNoAuthApi: AconAppNoAuthApi,
    private val aconAppApi: AconAppApi,
    private val fileUploadApi: FileUploadApi,
) {
    suspend fun fetchShouldUpdateApp(currentVersion: String): ShouldUpdateResponse {
        return aconAppNoAuthApi.fetchShouldUpdateApp(currentVersion)
    }

    suspend fun getPresignedUrl(request: GetPresignedUrlRequest): PresignedUrlResponse {
        return aconAppApi.getPresignedUrl(request)
    }

    suspend fun uploadFile(presignedUrl: String, body: RequestBody) {
        fileUploadApi.uploadFile(presignedUrl, body)
    }
}