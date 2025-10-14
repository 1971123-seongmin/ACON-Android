package com.acon.core.data.repository

import com.acon.acon.core.model.model.OnboardingPreferences
import com.acon.acon.core.model.type.FoodType
import com.acon.acon.domain.error.onboarding.PostTastePreferenceResultError
import com.acon.acon.domain.error.onboarding.VerifyAreaError
import com.acon.acon.domain.repository.OnboardingRepository
import com.acon.core.data.stream.DataStream
import com.acon.core.data.datasource.local.OnboardingLocalDataSource
import com.acon.core.data.datasource.remote.OnboardingRemoteDataSource
import com.acon.core.data.di.VerifiedArea
import com.acon.core.data.dto.request.TastePreferenceRequest
import com.acon.core.data.error.runCatchingWith
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val onboardingRemoteDataSource: OnboardingRemoteDataSource,
    private val onboardingLocalDataSource: OnboardingLocalDataSource,
    @VerifiedArea private val areaDataStream: DataStream
) : OnboardingRepository {

    override suspend fun submitTastePreferenceResult(
        dislikeFoods: List<FoodType>
    ): Result<Unit> {
        return runCatchingWith(PostTastePreferenceResultError()) {
            val request = TastePreferenceRequest(dislikeFoods = dislikeFoods.map { it.name })
            onboardingRemoteDataSource.submitTastePreferenceResult(request)
            onboardingLocalDataSource.updateShouldChooseDislikes(false)
        }
    }

    override suspend fun verifyArea(
        latitude: Double,
        longitude: Double
    ): Result<Unit> = runCatchingWith(VerifyAreaError()) {
        onboardingRemoteDataSource.verifyArea(
            latitude = latitude,
            longitude = longitude
        )
        onboardingLocalDataSource.updateShouldVerifyArea(false)
        areaDataStream.notifyDataChanged()
    }

    override suspend fun updateShouldChooseDislikes(shouldChoose: Boolean): Result<Unit> {
        return runCatchingWith {
            onboardingLocalDataSource.updateShouldChooseDislikes(shouldChoose)
        }
    }

    override suspend fun updateShouldShowIntroduce(shouldShow: Boolean): Result<Unit> {
        return runCatchingWith {
            onboardingLocalDataSource.updateShouldShowIntroduce(shouldShow)
        }
    }

    override suspend fun updateShouldVerifyArea(shouldVerify: Boolean): Result<Unit> {
        return runCatchingWith {
            onboardingLocalDataSource.updateShouldVerifyArea(shouldVerify)
        }
    }

    override suspend fun getOnboardingPreferences(): Result<OnboardingPreferences> {
        return runCatchingWith {
            val entity = onboardingLocalDataSource.getOnboardingPreferences()
            OnboardingPreferences(
                shouldShowIntroduce = entity.shouldShowIntroduce,
                shouldChooseDislikes = entity.shouldChooseDislikes,
                shouldVerifyArea = entity.shouldVerifyArea
            )
        }
    }
}