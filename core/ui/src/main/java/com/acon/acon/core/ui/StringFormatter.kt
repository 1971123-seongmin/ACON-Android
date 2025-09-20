package com.acon.acon.core.ui

interface StringFormatter {
    fun format(s: String): String
}

/**
 * `yyyy.MM.dd` to `yyyyMMdd`
 * ```
 * DateStringFormatter.format("2025.09.15") // == 20250915
 */
object DateStringFormatter : StringFormatter {

    override fun format(s: String): String {
        val trimmed = if (s.length >= 8) s.substring(0..7) else s
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 3 || i == 5) {
                out += "."
            }
        }

        return out
    }
}