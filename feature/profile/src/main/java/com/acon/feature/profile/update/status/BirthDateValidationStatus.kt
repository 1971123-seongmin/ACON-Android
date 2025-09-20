package com.acon.feature.profile.update.status

sealed interface BirthDateValidationStatus {
    data object Idle: BirthDateValidationStatus
    data object Typing: BirthDateValidationStatus
    data object Valid: BirthDateValidationStatus
    data object Invalid: BirthDateValidationStatus
}