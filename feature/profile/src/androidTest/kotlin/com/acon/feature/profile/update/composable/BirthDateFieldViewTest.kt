package com.acon.feature.profile.update.composable

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.input.TextFieldValue
import com.acon.acon.core.ui.test.getAlpha
import com.acon.feature.profile.TestTags
import com.acon.feature.profile.update.status.BirthDateValidationStatus
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class BirthDateFieldViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `생년월일_유효성_상태가_유효하지않음_이면_유효성_경고_행이_보인다`() {
        // Given
        val dummyInput = TextFieldValue("")
        composeTestRule.setContent {
            BirthDateFieldView(
                input = dummyInput,
                onInputChange = mockk(),
                validationStatus = BirthDateValidationStatus.Invalid
            )
        }

        // When
        val birthDateValidationViewNode = composeTestRule.onNodeWithTag(TestTags.BIRTH_DATE_VALIDATION_VIEW)

        // Then
        assertEquals(1f, birthDateValidationViewNode.getAlpha())
    }

    @Test
    fun `생년월일_유효성_상태가_초기상태_이면_유효성_경고_행이_보이지_않는다`() {
        // Given
        val dummyInput = TextFieldValue("")
        composeTestRule.setContent {
            BirthDateFieldView(
                input = dummyInput,
                onInputChange = mockk(),
                validationStatus = BirthDateValidationStatus.Idle
            )
        }

        // When
        val birthDateValidationViewNode = composeTestRule.onNodeWithTag(TestTags.BIRTH_DATE_VALIDATION_VIEW)

        // Then
        assertEquals(0f, birthDateValidationViewNode.getAlpha())
    }

    @Test
    fun `생년월일_유효성_상태가_입력중_이면_유효성_경고_행이_보이지_않는다`() {
        // Given
        val dummyInput = TextFieldValue("")
        composeTestRule.setContent {
            BirthDateFieldView(
                input = dummyInput,
                onInputChange = mockk(),
                validationStatus = BirthDateValidationStatus.Typing
            )
        }

        // When
        val birthDateValidationViewNode = composeTestRule.onNodeWithTag(TestTags.BIRTH_DATE_VALIDATION_VIEW)

        // Then
        assertEquals(0f, birthDateValidationViewNode.getAlpha())
    }

    @Test
    fun `생년월일_유효성_상태가_유효함_이면_유효성_경고_행이_보이지_않는다`() {
        // Given
        val dummyInput = TextFieldValue("")
        composeTestRule.setContent {
            BirthDateFieldView(
                input = dummyInput,
                onInputChange = mockk(),
                validationStatus = BirthDateValidationStatus.Valid
            )
        }

        // When
        val birthDateValidationViewNode = composeTestRule.onNodeWithTag(TestTags.BIRTH_DATE_VALIDATION_VIEW)

        // Then
        assertEquals(0f, birthDateValidationViewNode.getAlpha())
    }
}
