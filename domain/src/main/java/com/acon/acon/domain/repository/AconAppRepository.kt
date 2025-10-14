package com.acon.acon.domain.repository

import com.acon.acon.core.model.type.ImageType

interface AconAppRepository {
    suspend fun shouldUpdateApp(currentVersion: String): Result<Boolean>
    
    /**
     * 이미지를 서버에 업로드
     *
     * @param imageType 업로드할 이미지 유형
     * @param url 이미지 파일의 로컬 경로
     * @return 업로드된 이미지의 최종 URL
     * @see ImageType
     */
    suspend fun uploadImage(imageType: ImageType, url: String): Result<String>
}