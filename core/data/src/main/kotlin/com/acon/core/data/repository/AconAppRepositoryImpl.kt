package com.acon.core.data.repository

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.acon.acon.core.model.type.ImageType
import com.acon.core.data.datasource.remote.AconAppRemoteDataSource
import com.acon.core.data.error.runCatchingWith
import com.acon.acon.domain.error.app.FetchShouldUpdateError
import com.acon.acon.domain.repository.AconAppRepository
import com.acon.core.data.dto.request.GetPresignedUrlRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import kotlin.collections.contains

class AconAppRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val aconAppRemoteDataSource: AconAppRemoteDataSource
) : AconAppRepository {

    private val availableImageMimeTypes by lazy {
        setOf("image/jpg", "image/jpeg", "image/png", "image/webp", "image/heic")
    }

    override suspend fun shouldUpdateApp(currentVersion: String): Result<Boolean> {
        return runCatchingWith(FetchShouldUpdateError()) {
            aconAppRemoteDataSource.fetchShouldUpdateApp(currentVersion).shouldUpdate ?: false
        }
    }

    override suspend fun uploadImage(imageType: ImageType, url: String): Result<String> {
        return runCatchingWith {
            val contentUri = url.toUri()
            val fileName = DocumentFile.fromSingleUri(context, contentUri)?.name
                ?: error("Failed to read file name: $url")

            val presignedUrlResponse = aconAppRemoteDataSource.getPresignedUrl(GetPresignedUrlRequest(
                imageType = imageType,
                fileName = fileName
            ))

            val uriMimeType = context.contentResolver.getType(contentUri)
            val finalMimeType = if (availableImageMimeTypes.contains(uriMimeType)) uriMimeType!! else "image/jpeg"

            val inputStream = context.contentResolver.openInputStream(contentUri)
            val requestBody = inputStream?.use { input ->
                input.readBytes().toRequestBody(finalMimeType.toMediaTypeOrNull())
            } ?: error("Failed to read image content: $url")

            aconAppRemoteDataSource.uploadFile(presignedUrlResponse.presignedUrl, requestBody)

            return@runCatchingWith presignedUrlResponse.fileUrl
        }
    }
}