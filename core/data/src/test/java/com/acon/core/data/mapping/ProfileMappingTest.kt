package com.acon.core.data.mapping

import com.acon.acon.core.model.model.profile.BirthDateStatus
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import com.acon.core.data.dto.response.profile.ProfileResponse
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Month
import kotlin.test.assertEquals
import kotlin.test.assertIs

@ExtendWith(MockKExtension::class)
class ProfileMappingTest {

    @Test
    fun `nickname Response는 그대로 전달한다`() {
        // Given
        val expectedNickname = "Nickname Response"
        val sampleProfileResponse = ProfileResponse(
            nickname = expectedNickname,
            birthDate = null,
            image = null
        )

        // When
        val actualProfile = sampleProfileResponse.toProfile()
        val actualNickname = actualProfile.nickname

        // Then
        assertEquals(actualNickname, expectedNickname)
    }

    @Test
    fun `birthDate Response가 null이 아닐 경우 생일을 지정됨 상태로 변환한다`() {
        // Given
        val expectedYear = 1999
        val expectedMonth = Month.APRIL
        val expectedDayOfMonth = 29

        val sampleBirthDateString = "1999.04.29"
        val sampleProfileResponse = ProfileResponse(
            nickname = "Dummy Nickname",
            birthDate = sampleBirthDateString,
            image = "Dummy Image Url"
        )

        // When
        val actualProfile = sampleProfileResponse.toProfile()
        val actualBirthDateStatus = actualProfile.birthDate

        // Then
        assertIs<BirthDateStatus.Specified>(actualBirthDateStatus)
        actualBirthDateStatus.date.let { actual ->
            assertEquals(expectedYear, actual.year)
            assertEquals(expectedMonth, actual.month)
            assertEquals(expectedDayOfMonth, actual.dayOfMonth)
        }
    }

    @Test
    fun `birthDate Response가 null일 경우 생일을 지정되지 않음 상태로 변환한다`() {
        // Given
        val sampleProfileResponse = ProfileResponse(
            nickname = "Dummy Nickname",
            birthDate = null,
            image = "Dummy Image Url"
        )

        // When
        val actualProfile = sampleProfileResponse.toProfile()
        val actualBirthDateStatus = actualProfile.birthDate

        // Then
        assertIs<BirthDateStatus.NotSpecified>(actualBirthDateStatus)
    }

    @Test
    fun `birthDate Response가 null이 아닐 때, 파싱에 실패하면 생일을 지정되지 않음 상태로 변환한다`() {
        // Given
        val sampleInvalidBirthDateFormat = "1999-04-29"
        val sampleProfileResponse = ProfileResponse(
            nickname = "Dummy Nickname",
            birthDate = sampleInvalidBirthDateFormat,
            image = "Dummy Image Url"
        )

        // When
        val actualProfile = sampleProfileResponse.toProfile()
        val actualBirthDateStatus = actualProfile.birthDate

        // Then
        assertIs<BirthDateStatus.NotSpecified>(actualBirthDateStatus)
    }

    @Test
    fun `image Response가 null이 아닐 경우 이미지를 커스텀 상태로 변환한다`() {
        // Given
        val expectedImageUrl = "Custom Profile Image Url"
        val sampleProfileResponse = ProfileResponse(
            nickname = "Dummy Nickname",
            birthDate = "Dummy BirthDate",
            image = expectedImageUrl
        )

        // When
        val actualProfile = sampleProfileResponse.toProfile()
        val actualProfileImageStatus = actualProfile.image

        // Then
        assertIs<ProfileImageStatus.Custom>(actualProfileImageStatus)
        assertEquals(expectedImageUrl, (actualProfileImageStatus).url)
    }

    @Test
    fun `image Response가 null일 경우 이미지를 디폴트 상태로 변환한다`() {
        // Given
        val sampleProfileResponse = ProfileResponse(
            nickname = "Dummy Nickname",
            birthDate = "Dummy BirthDate",
            image = null
        )

        // When
        val actualProfile = sampleProfileResponse.toProfile()
        val actualProfileImageStatus = actualProfile.image

        // Then
        assertIs<ProfileImageStatus.Default>(actualProfileImageStatus)
    }
}