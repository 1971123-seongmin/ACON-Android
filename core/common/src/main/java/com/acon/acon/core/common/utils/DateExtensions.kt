package com.acon.acon.core.common.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val yyyyMMddFormatter by lazy {
    DateTimeFormatter.ofPattern("yyyy.MM.dd")
}

/**
 * LocalDate를 yyyy.MM.dd 형식으로 변환
 */
fun LocalDate.toyyyyMMdd(): String {
    return format(yyyyMMddFormatter)
}