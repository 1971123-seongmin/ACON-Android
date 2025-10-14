package com.acon.acon.domain.usecase

import com.acon.acon.domain.error.profile.ValidateBirthDateError
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot

@ExtendWith(MockKExtension::class)
class ValidateBirthDateUseCaseTest {

    private lateinit var validateBirthDateUseCase: ValidateBirthDateUseCase

    @BeforeEach
    fun setUp() {
        validateBirthDateUseCase = ValidateBirthDateUseCase()
    }

    @Test
    fun `validateBirthDateUseCase()는 입력 생일이 1900년 이전일 경우 예외 객체를 Result Wrapping하여 반환한다`() = runTest {
        // Given
        val sampleLocalDate = LocalDate.of(1899,12,31)

        // When
        val actualException = validateBirthDateUseCase(sampleLocalDate).exceptionOrNull()

        // Then
        assertIs<ValidateBirthDateError.InputIsTooPast>(actualException)
    }

    @Test
    fun `validateBirthDateUseCase()는 입력 생일이 1900년 1월 1일일 경우 '너무 과거'예외를 반환하지 않는다`() = runTest {
        // Given
        val sampleLocalDate = LocalDate.of(1900, 1, 1)

        // When
        val actualException = validateBirthDateUseCase(sampleLocalDate).exceptionOrNull()

        // Then
        assertIsNot<ValidateBirthDateError.InputIsTooPast>(actualException)
    }

    @Test
    fun `validateBirthDateUseCase()는 입력 생일이 오늘 이후일 경우 예외 객체를 Result Wrapping하여 반환한다`() = runTest {
        // Given
        val sampleLocalDate = LocalDate.now().plusDays(1)

        val actualException = validateBirthDateUseCase(sampleLocalDate).exceptionOrNull()

        assertIs<ValidateBirthDateError.InputIsFuture>(actualException)
    }

    @Test
    fun `validateBirthDateUseCase()는 입력 생일이 오늘일 경우 '미래'예외를 반환하지 않는다`() = runTest {
        // Given
        val sampleLocalDate = LocalDate.now()

        val actualException = validateBirthDateUseCase(sampleLocalDate).exceptionOrNull()

        assertIsNot<ValidateBirthDateError.InputIsFuture>(actualException)
    }

    @Test
    fun `validateBirthDateUseCase()는 유효한 생일일 경우 성공처리 한다`() = runTest {
        // Given
        val sampleLocalDate = LocalDate.of(1999, 4, 29)
        val expectedResult = Result.success(Unit)

        // When
        val actualResult = validateBirthDateUseCase(sampleLocalDate)

        // Then
        assertEquals(expectedResult, actualResult)
    }
}