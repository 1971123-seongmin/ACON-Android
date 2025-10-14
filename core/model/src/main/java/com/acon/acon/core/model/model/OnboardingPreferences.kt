package com.acon.acon.core.model.model

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingPreferences(
    val shouldShowIntroduce: Boolean,
    val shouldChooseDislikes: Boolean,
    val shouldVerifyArea: Boolean
)
