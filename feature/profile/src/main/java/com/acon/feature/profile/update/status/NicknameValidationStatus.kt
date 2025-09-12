package com.acon.feature.profile.update.status

sealed interface NicknameValidationStatus {
    data object Idle: NicknameValidationStatus
    data object Available: NicknameValidationStatus
    data object AlreadyExist: NicknameValidationStatus
    data object Empty: NicknameValidationStatus
    data object InvalidFormat: NicknameValidationStatus
}