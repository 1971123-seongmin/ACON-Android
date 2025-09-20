package com.acon.acon.domain.repository

import com.acon.acon.core.model.model.area.Area
import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.SavedSpot
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun getProfile() : Flow<Result<Profile>>
    suspend fun updateProfile(newProfile: Profile) : Result<Unit>
    suspend fun validateNickname(nickname: String) : Result<Unit>
    suspend fun getSavedSpots() : Flow<Result<List<SavedSpot>>>
    fun getVerifiedAreas(): Flow<Result<List<Area>>>
    suspend fun deleteVerifiedArea(verifiedAreaId: Long): Result<Unit>
}