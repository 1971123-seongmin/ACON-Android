package com.acon.feature.profile.update.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acon.acon.core.designsystem.R
import com.acon.acon.core.designsystem.component.textfield.v2.AconOutlinedTextField
import com.acon.acon.core.designsystem.theme.AconTheme
import com.acon.acon.core.ui.test.testAlpha
import com.acon.acon.domain.usecase.ValidateNicknameUseCase.Companion.MAX_NICKNAME_LENGTH
import com.acon.feature.profile.TestTags
import com.acon.feature.profile.update.status.NicknameValidationStatus

@Composable
internal fun NicknameFieldView(
    input: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    validationStatus: NicknameValidationStatus,
    modifier: Modifier = Modifier
) {

    val validationResultAlpha = when(validationStatus) {
        is NicknameValidationStatus.Idle -> 0f
        is NicknameValidationStatus.Loading -> 0f
        else -> 1f
    }

    val validationResultMessageResId = when (validationStatus) {
        NicknameValidationStatus.Idle, NicknameValidationStatus.Loading -> null
        NicknameValidationStatus.Empty -> R.string.nickname_error_empty
        NicknameValidationStatus.AlreadyExist -> R.string.nickname_error_duplicate
        NicknameValidationStatus.InvalidFormat -> R.string.nickname_error_invalid
        NicknameValidationStatus.Available -> R.string.nickname_valid
    }
    val validationResultIcon = ImageVector.vectorResource(
        when (validationStatus) {
            NicknameValidationStatus.Available -> R.drawable.ic_valid
            else -> R.drawable.ic_error
        }
    )
    val validationResultUIColor = when (validationStatus) {
        NicknameValidationStatus.Available -> AconTheme.color.Success
        else -> AconTheme.color.Danger
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.nickname_textfield_title),
                style = AconTheme.typography.Title4,
                fontWeight = FontWeight.SemiBold,
                color = AconTheme.color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.star),
                style = AconTheme.typography.Title4,
                fontWeight = FontWeight.SemiBold,
                color = AconTheme.color.Gray50
            )
        }

        AconOutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
        ) { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                innerTextField()
                Spacer(modifier = Modifier.weight(1f))
                if (validationStatus is NicknameValidationStatus.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = AconTheme.color.Gray600
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .clickable { onInputChange(TextFieldValue()) }
                            .size(18.dp)
                            .alpha(
                                if (validationStatus is NicknameValidationStatus.Empty) 0f else 1f
                            ),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_clear),
                        contentDescription = stringResource(R.string.clear_search_content_description),
                        tint = AconTheme.color.Gray50,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(validationResultAlpha).testTag(
                    TestTags.NICKNAME_VALIDATION_RESULT_VIEW
                ).semantics {
                    testAlpha = validationResultAlpha
                }
            ) {
                Icon(
                    imageVector = validationResultIcon,
                    contentDescription = null,
                    tint = Color.Unspecified
                )

                if (validationResultMessageResId != null)
                    Text(
                        text = stringResource(validationResultMessageResId),
                        style = AconTheme.typography.Body1,
                        color = validationResultUIColor,
                        modifier = Modifier.padding(start = 4.dp)
                    )
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(
                    R.string.nickname_input_count,
                    input.text.length,
                    MAX_NICKNAME_LENGTH
                ),
                style = AconTheme.typography.Caption1,
                color = AconTheme.color.Gray500,
            )
        }
    }
}

@Preview
@Composable
private fun NicknameFieldViewPreview() {
    NicknameFieldView(
        input = TextFieldValue("acon123"),
        onInputChange = {},
        validationStatus = NicknameValidationStatus.Idle,
    )
}