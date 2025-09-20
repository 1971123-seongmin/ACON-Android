package com.acon.feature.profile.update.status

sealed interface ProfileImageInputStatus {
    data object Changed: ProfileImageInputStatus
    data object NotChanged: ProfileImageInputStatus
}