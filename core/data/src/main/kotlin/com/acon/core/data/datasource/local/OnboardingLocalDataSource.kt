package com.acon.core.data.datasource.local

import androidx.datastore.core.DataStore
import com.acon.core.data.dto.entity.OnboardingPreferencesEntity
import com.acon.core.data.dto.entity.copy
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OnboardingLocalDataSource @Inject constructor(
    private val onboardingDataStore: DataStore<OnboardingPreferencesEntity>
) {

    suspend fun updateOnboardingPreferences(pref: OnboardingPreferencesEntity) {
        onboardingDataStore.updateData { prefs ->
            prefs.copy {
                shouldShowIntroduce = pref.shouldShowIntroduce
                shouldChooseDislikes = pref.shouldChooseDislikes
                shouldVerifyArea = pref.shouldVerifyArea
            }
        }
    }

    suspend fun updateShouldShowIntroduce(shouldShow: Boolean) {
        onboardingDataStore.updateData { prefs ->
            prefs.copy {
                shouldShowIntroduce = shouldShow
            }
        }
    }

    suspend fun updateShouldChooseDislikes(shouldChooseDislikes: Boolean) {
        onboardingDataStore.updateData { prefs ->
            prefs.copy {
                this.shouldChooseDislikes = shouldChooseDislikes
            }
        }
    }

    suspend fun updateShouldVerifyArea(shouldVerifyArea: Boolean) {
        onboardingDataStore.updateData { prefs ->
            prefs.copy {
                this.shouldVerifyArea = shouldVerifyArea
            }
        }
    }

    suspend fun getOnboardingPreferences(): OnboardingPreferencesEntity {
        return onboardingDataStore.data.first()
    }
}