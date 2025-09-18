package com.acon.feature.profile.update.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.acon.acon.core.designsystem.R
import com.acon.acon.core.designsystem.component.textfield.v2.AconOutlinedTextField
import com.acon.acon.core.designsystem.theme.AconTheme
import com.acon.acon.core.ui.DateVisualTransformation
import com.acon.acon.core.ui.test.testAlpha
import com.acon.feature.profile.TestTags
import com.acon.feature.profile.update.status.BirthDateValidationStatus

@Composable
internal fun BirthDateFieldView(
    input: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    validationStatus: BirthDateValidationStatus,
    modifier: Modifier = Modifier
) {

    val validationViewAlpha = when(validationStatus) {
        BirthDateValidationStatus.Invalid -> 1f
        else -> 0f
    }

    val validationResultMessageResId = when (validationStatus) {
        BirthDateValidationStatus.Idle -> null
        BirthDateValidationStatus.Valid -> null
        BirthDateValidationStatus.Typing -> null
        BirthDateValidationStatus.Invalid -> R.string.birthday_error_invalid
    }
    val validationResultIcon = ImageVector.vectorResource(
        R.drawable.ic_error
    )

    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.birthdate_field_title),
            style = AconTheme.typography.Title4,
            fontWeight = FontWeight.SemiBold,
            color = AconTheme.color.White
        )

        AconOutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            visualTransformation = DateVisualTransformation,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 4.dp)
                .alpha(validationViewAlpha).testTag(TestTags.BIRTH_DATE_VALIDATION_VIEW).semantics {
                    this.testAlpha = validationViewAlpha
                },
            verticalAlignment = Alignment.CenterVertically
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
                    color = AconTheme.color.Danger,
                    modifier = Modifier.padding(start = 4.dp)
                )
        }
    }
}
