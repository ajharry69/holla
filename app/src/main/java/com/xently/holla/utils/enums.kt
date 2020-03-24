package com.xently.holla.utils

import java.util.*

/*
val v = if (isCaseSensitive) value else value.toLowerCase(Locale.ROOT)
                for (sb in values()) {
                    val sbV = sb.value
                    if ((if (isCaseSensitive) sbV else sbV.toLowerCase(Locale.ROOT)) == v) return sb
                }
                return null
 */
interface IEnum<T : Enum<*>> {
    fun fromValue(value: String, isCaseSensitive: Boolean = true): T?

    fun fromValueOrDefault(
        value: String,
        default: T,
        isCaseSensitive: Boolean = true
    ): T = fromValue(value, isCaseSensitive) ?: default
}

enum class DurationUnit(val equiv: String) {
    MILLISECONDS("MILLISECONDLY"),
    SECONDS("SECONDLY"),
    MINUTES("MINUTELY"),
    HOURS("HOURLY"),
    DAYS("DAILY"),
    WEEKS("WEEKLY"),
    MONTHS("MONTHLY"),
    YEARS("YEARLY");

    companion object : IEnum<DurationUnit> {
        override fun fromValue(value: String, isCaseSensitive: Boolean): DurationUnit? {
            val v = if (isCaseSensitive) value else value.toLowerCase(Locale.ROOT)
            for (sb in values()) {
                val sbV = sb.equiv
                if ((if (isCaseSensitive) sbV else sbV.toLowerCase(Locale.ROOT)) == v) return sb
            }
            return null
        }
    }
}

enum class Type { CREATE, UPDATE }