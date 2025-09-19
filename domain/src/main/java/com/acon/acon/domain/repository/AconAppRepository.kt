package com.acon.acon.domain.repository

import com.acon.acon.core.model.type.ImageType

interface AconAppRepository {
    suspend fun shouldUpdateApp(currentVersion: String): Result<Boolean>
    suspend fun uploadImage(imageType: ImageType, url: String): Result<String>
}