package com.acon.core.data.datasource.local

import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.SavedSpot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface ProfileLocalDataSource {

    suspend fun cacheProfile(profile: Profile)
    fun getProfile() : Flow<Profile?>

    suspend fun cacheSavedSpots(savedSpots: List<SavedSpot>)
    fun getSavedSpots(): Flow<List<SavedSpot>?>

    suspend fun clearCache()
}

class ProfileLocalDataSourceImpl @Inject constructor(

) : ProfileLocalDataSource {

    private val _profile = MutableStateFlow<Profile?>(null)
    private val profile = _profile.asStateFlow()

    private val _savedSpots = MutableStateFlow<List<SavedSpot>?>(null)
    private val savedSpots = _savedSpots.asStateFlow()

    override suspend fun cacheProfile(profile: Profile) {
        _profile.value = profile
    }

    override fun getProfile(): Flow<Profile?> {
        return profile
    }

    override suspend fun cacheSavedSpots(savedSpots: List<SavedSpot>) {
        _savedSpots.value = savedSpots
    }

    override fun getSavedSpots(): Flow<List<SavedSpot>?> {
        return savedSpots
    }

    override suspend fun clearCache() {
        _profile.value = null
        _savedSpots.value = null
    }
}