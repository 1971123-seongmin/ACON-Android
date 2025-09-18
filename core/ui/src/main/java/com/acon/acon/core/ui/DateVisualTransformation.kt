package com.acon.acon.core.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * 입력 텍스트를 날짜 형식으로 변환하는 [VisualTransformation]입니다.
 *
 * 이 변환은 "yyyy.MM.dd" 형식에 맞게 .을 자동으로 추가합니다.
 * 예를 들어, "20250915"을 입력하면 "2025.09.15"으로 표시됩니다. 사용자가 문자를
 * 입력하거나 삭제할 때 커서 위치를 올바르게 처리합니다.
 *
 * ```
 * TextField(
 *     value = text,
 *     onValueChange = { text = it.filter { char -> char.isDigit() }.take(8) },
 *     visualTransformation = DateVisualTransformation()
 * )
 * ```
 */
object DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {

        val formatter = DateStringFormatter
        val out = formatter.format(text.text)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}