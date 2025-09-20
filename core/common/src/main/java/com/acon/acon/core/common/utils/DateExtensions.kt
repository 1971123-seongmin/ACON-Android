package com.acon.acon.core.common.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val yyyyMMddFormatter by lazy {
    DateTimeFormatter.ofPattern("yyyyMMdd")
}

/**
 * [LocalDate]를 `yyyyMMdd` 형식으로 변환
 */
fun LocalDate.toyyyyMMdd(): String {
    return format(yyyyMMddFormatter)
}

/**
 * yyyyMMdd을 [LocalDate]로 변환.
 * 파싱 실패 시 null 반환
 * ```
 * "20250915".toLocalDate()    // == LocalDate.of(2025, 9, 15)
 * ```
 */
fun String.toLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this, yyyyMMddFormatter)
    } catch (_: DateTimeParseException) {
        null
    }
}