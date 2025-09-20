package com.acon.core.data.api.remote.noauth

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

interface FileUploadApi {
    @PUT
    suspend fun uploadFile(
        @Url url: String,
        @Body body: RequestBody
    )
}