package com.acon.core.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.acon.acon.core.common.IODispatcher
import com.acon.acon.core.model.type.ImageType
import com.acon.acon.domain.error.app.FetchShouldUpdateError
import com.acon.acon.domain.repository.AconAppRepository
import com.acon.core.data.datasource.remote.AconAppRemoteDataSource
import com.acon.core.data.dto.request.GetPresignedUrlRequest
import com.acon.core.data.dto.response.PresignedUrlResponse
import com.acon.core.data.error.runCatchingWith
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AconAppRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
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

            return@runCatchingWith withContext(dispatcher) {
                val presignedUrlResponseDeferred = async {
                    contentUri.getPresignedUrlResponse(imageType)
                }
                val requestBodyDeferred = async {
                    contentUri.getRequestBody()
                }

                val presignedUrlResponse = presignedUrlResponseDeferred.await()
                val requestBody = requestBodyDeferred.await()
                aconAppRemoteDataSource.uploadFile(
                    presignedUrlResponse.presignedUrl,
                    requestBody
                )

                return@withContext presignedUrlResponse.fileUrl
            }
        }
    }

    private suspend fun Uri.getPresignedUrlResponse(imageType: ImageType): PresignedUrlResponse {
        val fileName = DocumentFile.fromSingleUri(context, this)?.name
            ?: error("Failed to read file name: $this")

        return aconAppRemoteDataSource.getPresignedUrl(
            GetPresignedUrlRequest(
                imageType = imageType,
                fileName = fileName
            )
        )
    }

    private fun Uri.getRequestBody(): RequestBody {
        val uriMimeType = context.contentResolver.getType(this)
        val finalMimeType = if (availableImageMimeTypes.contains(uriMimeType)) uriMimeType!! else "image/jpeg"

        val inputStream = context.contentResolver.openInputStream(this)
        return inputStream?.use { input ->
            input.readBytes().toRequestBody(finalMimeType.toMediaTypeOrNull())
        } ?: error("Failed to read image content: $this")
    }
}