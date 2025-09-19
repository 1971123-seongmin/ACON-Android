package com.acon.core.data.repository

import android.content.Context
import androidx.core.net.toUri
import com.acon.acon.core.model.model.area.Area
import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import com.acon.acon.core.model.model.profile.SavedSpot
import com.acon.acon.core.model.type.ImageType
import com.acon.acon.domain.error.area.DeleteVerifiedAreaError
import com.acon.acon.domain.error.profile.UpdateProfileError
import com.acon.acon.domain.error.profile.ValidateNicknameError
import com.acon.acon.domain.repository.ProfileRepository
import com.acon.core.data.stream.DataStream
import com.acon.core.data.datasource.local.ProfileLocalDataSource
import com.acon.core.data.datasource.remote.AconAppRemoteDataSource
import com.acon.core.data.datasource.remote.ProfileRemoteDataSource
import com.acon.core.data.di.VerifiedArea
import com.acon.core.data.dto.request.GetPresignedUrlRequest
import com.acon.core.data.dto.request.profile.toUpdateProfileRequest
import com.acon.core.data.error.runCatchingWith
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource,
    private val profileLocalDataSource: ProfileLocalDataSource,
    private val aconAppRemoteDataSource: AconAppRemoteDataSource,
    @VerifiedArea private val areaDataStream: DataStream,
    @ApplicationContext private val context: Context
) : ProfileRepository {

    override fun getProfile(): Flow<Result<Profile>> {
        return profileLocalDataSource.getProfile().flatMapLatest { cachedProfile: Profile? ->
            if (cachedProfile == null) {
                getProfileFromRemote()
            } else {
                flowOf(Result.success(cachedProfile))
            }
        }
    }

    private fun getProfileFromRemote(): Flow<Result<Profile>> {
        return flow {
            emit(runCatchingWith {
                val profileResponse = profileRemoteDataSource.getProfile()
                val profile = profileResponse.toProfile()

                profileLocalDataSource.cacheProfile(profile)

                profile
            })
        }
    }

    override suspend fun updateProfile(newProfile: Profile): Result<Unit> {
        return runCatchingWith(UpdateProfileError()) {
            val profileToUpdate: Profile

            val imageStatus = newProfile.image
            if (imageStatus is ProfileImageStatus.Custom) {
                if (imageStatus.url.startsWith("content://")) {
                    val presignedUrlResponse = aconAppRemoteDataSource.getPresignedUrl(GetPresignedUrlRequest(
                        imageType = ImageType.PROFILE,
                        fileName = imageStatus.url
                    ))
                    val inputStream = context.contentResolver.openInputStream(imageStatus.url.toUri())
                    val requestBody = inputStream?.readBytes()?.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    requestBody?.let {
                        aconAppRemoteDataSource.uploadFile(presignedUrlResponse.presignedUrl, it)
                    }

                    profileToUpdate = newProfile.copy(
                        image = ProfileImageStatus.Custom(presignedUrlResponse.fileUrl)
                    )
                } else {
                    profileToUpdate = newProfile
                }
            } else {
                profileToUpdate = newProfile
            }

            profileRemoteDataSource.updateProfile(profileToUpdate.toUpdateProfileRequest())

            profileLocalDataSource.cacheProfile(profileToUpdate)

            Unit
        }
    }

    override suspend fun validateNickname(nickname: String) : Result<Unit> {
        return runCatchingWith(ValidateNicknameError()) {
            profileRemoteDataSource.validateNickname(nickname)
        }
    }

    override suspend fun getSavedSpots(): Flow<Result<List<SavedSpot>>> {
        return profileLocalDataSource.getSavedSpots().flatMapLatest { cachedSavedSpots: List<SavedSpot>? ->
            if (cachedSavedSpots == null) {
                getSavedSpotsFromRemote()
            } else {
                flowOf(Result.success(cachedSavedSpots))
            }
        }
    }

    private fun getSavedSpotsFromRemote(): Flow<Result<List<SavedSpot>>> {
        return flow {
            emit(runCatchingWith {
                val savedSpotResponses = profileRemoteDataSource.getSavedSpots()
                val savedSpots = savedSpotResponses.map { it.toSavedSpot() }

                profileLocalDataSource.cacheSavedSpots(savedSpots)

                savedSpots
            })
        }
    }

    override fun getVerifiedAreas(): Flow<Result<List<Area>>> {
        return areaDataStream.subscribe {
            emit(runCatchingWith() {
                profileRemoteDataSource.getVerifiedAreas().verifiedAreaList
                    .map { it.toVerifiedArea() }
            })
        }
    }

    override suspend fun deleteVerifiedArea(verifiedAreaId: Long): Result<Unit> {
        return runCatchingWith(DeleteVerifiedAreaError()) {
            profileRemoteDataSource.deleteVerifiedArea(verifiedAreaId)
            areaDataStream.notifyDataChanged()
        }
    }
}
