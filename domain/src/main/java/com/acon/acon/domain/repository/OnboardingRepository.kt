package com.acon.acon.domain.repository

import com.acon.acon.core.model.model.OnboardingPreferences
import com.acon.acon.core.model.type.FoodType

interface OnboardingRepository {
    suspend fun submitTastePreferenceResult(
        dislikeFoods: List<FoodType>
    ): Result<Unit>

    suspend fun verifyArea(
        latitude: Double,
        longitude: Double
    ): Result<Unit>

    suspend fun updateShouldShowIntroduce(shouldShow: Boolean): Result<Unit>
    suspend fun updateShouldChooseDislikes(shouldChoose: Boolean): Result<Unit>
    suspend fun updateShouldVerifyArea(shouldVerify: Boolean): Result<Unit>

    suspend fun getOnboardingPreferences(): Result<OnboardingPreferences>
}
