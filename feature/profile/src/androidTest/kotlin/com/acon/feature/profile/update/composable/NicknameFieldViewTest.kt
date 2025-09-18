package com.acon.feature.profile.update.composable

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.input.TextFieldValue
import com.acon.acon.core.ui.test.getAlpha
import com.acon.feature.profile.TestTags
import com.acon.feature.profile.update.status.NicknameValidationStatus
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class NicknameFieldViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val dummyInput = TextFieldValue("")

    @Test
    fun `닉네임_유효성_상태가_초기상태이면_유효성_결과_뷰가_보이지_않는다`() {

        // Given
        composeTestRule.setContent {
            NicknameFieldView(
                input = dummyInput,
                onInputChange = mockk(),
                validationStatus = NicknameValidationStatus.Idle
            )
        }

        // When
        val nicknameValidationResultViewNode = composeTestRule.onNodeWithTag(TestTags.NICKNAME_VALIDATION_RESULT_VIEW)

        // Then
        assertEquals(0f, nicknameValidationResultViewNode.getAlpha())
    }

    @Test
    fun `닉네임_유효성_상태가_로딩이면_유효성_결과_뷰가_보이지_않는다`() {

        // Given
        composeTestRule.setContent {
            NicknameFieldView(
                input = dummyInput,
                onInputChange = mockk(),
                validationStatus = NicknameValidationStatus.Loading
            )
        }

        // When
        val nicknameValidationResultViewNode = composeTestRule.onNodeWithTag(TestTags.NICKNAME_VALIDATION_RESULT_VIEW)

        // Then
        assertEquals(0f, nicknameValidationResultViewNode.getAlpha())
    }

    @Test
    fun `닉네임_유효성_상태가_사용가능_이면_유효성_결과_뷰가_보인다`() {

        // Given
        composeTestRule.setContent {
            NicknameFieldView(
                input = dummyInput,
                onInputChange = mockk(),
                validationStatus = NicknameValidationStatus.Available
            )
        }

        // When
        val nicknameValidationResultViewNode = composeTestRule.onNodeWithTag(TestTags.NICKNAME_VALIDATION_RESULT_VIEW)

        // Then
        assertEquals(1f, nicknameValidationResultViewNode.getAlpha())
    }

    @Test
    fun `닉네임_유효성_상태가_빈입력_이면_유효성_결과_뷰가_보인다`() {

        // Given
        composeTestRule.setContent {
            NicknameFieldView(
                input = dummyInput,
                onInputChange = mockk(),
                validationStatus = NicknameValidationStatus.Empty
            )
        }

        // When
        val nicknameValidationResultViewNode = composeTestRule.onNodeWithTag(TestTags.NICKNAME_VALIDATION_RESULT_VIEW)

        // Then
        assertEquals(1f, nicknameValidationResultViewNode.getAlpha())
    }

    @Test
    fun `닉네임_유효성_상태가_잘못된형식_이면_유효성_결과_뷰가_보인다`() {

        // Given
        composeTestRule.setContent {
            NicknameFieldView(
                input = dummyInput,
                onInputChange = mockk(),
                validationStatus = NicknameValidationStatus.InvalidFormat
            )
        }

        // When
        val nicknameValidationResultViewNode = composeTestRule.onNodeWithTag(TestTags.NICKNAME_VALIDATION_RESULT_VIEW)

        // Then
        assertEquals(1f, nicknameValidationResultViewNode.getAlpha())
    }

    @Test
    fun `닉네임_유효성_상태가_중복닉네임_이면_유효성_결과_뷰가_보인다`() {

        // Given
        composeTestRule.setContent {
            NicknameFieldView(
                input = dummyInput,
                onInputChange = mockk(),
                validationStatus = NicknameValidationStatus.AlreadyExist
            )
        }

        // When
        val nicknameValidationResultViewNode = composeTestRule.onNodeWithTag(TestTags.NICKNAME_VALIDATION_RESULT_VIEW)

        // Then
        assertEquals(1f, nicknameValidationResultViewNode.getAlpha())
    }
}