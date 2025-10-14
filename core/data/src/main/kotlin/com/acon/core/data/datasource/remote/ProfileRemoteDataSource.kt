package com.acon.core.data.datasource.remote

import com.acon.core.data.api.remote.ProfileApi
import com.acon.core.data.dto.request.ReplaceVerifiedAreaRequest
import com.acon.core.data.dto.request.profile.UpdateProfileRequest
import com.acon.core.data.dto.response.area.VerifiedAreaListResponse
import com.acon.core.data.dto.response.profile.ProfileResponse
import com.acon.core.data.dto.response.profile.SavedSpotResponse
import javax.inject.Inject

interface ProfileRemoteDataSource {

    suspend fun getProfile() : ProfileResponse
    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest)
    suspend fun validateNickname(nickname: String)
    suspend fun getSavedSpots() : List<SavedSpotResponse>
    suspend fun getVerifiedAreas(): VerifiedAreaListResponse
    suspend fun deleteVerifiedArea(verifiedAreaId: Long)
}

class ProfileRemoteDataSourceImpl @Inject constructor(
    private val profileApi: ProfileApi
) : ProfileRemoteDataSource {

    override suspend fun getProfile(): ProfileResponse {
        return profileApi.getProfile()
    }

    override suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest) {
        profileApi.updateProfile(updateProfileRequest)
    }

    override suspend fun validateNickname(nickname: String) {
        profileApi.validateNickname(nickname)
    }

    override suspend fun getSavedSpots(): List<SavedSpotResponse> {
        return profileApi.getSavedSpots().savedSpotList
    }

    override suspend fun getVerifiedAreas(): VerifiedAreaListResponse {
        return profileApi.getVerifiedAreas()
    }

    override suspend fun deleteVerifiedArea(verifiedAreaId: Long) {
        return profileApi.deleteVerifiedArea(verifiedAreaId)
    }
}
