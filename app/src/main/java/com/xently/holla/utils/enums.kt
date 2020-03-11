package com.xently.holla.utils

import com.xently.holla.utils.PasswordOperationType.CHANGE
import com.xently.holla.utils.PasswordOperationType.RESET
import com.xently.holla.utils.VerificationCodeRequest.RESEND
import com.xently.holla.utils.VerificationCodeRequest.VERIFY
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

/**
 * 1). [RESET]: Flag to [RESET] a forgotten password
 * 2). [CHANGE]: Flag to [CHANGE] a password that's not forgotten
 */
enum class PasswordOperationType {
    /**
     * Flag to [RESET] a forgotten password
     */
    RESET,

    /**
     * Flag to [CHANGE] a password that's not forgotten
     */
    CHANGE
}

/**
 * 1). [RESEND]: Flag to [RESEND] verification code
 * 2). [VERIFY]: Flag to [VERIFY] verification code
 */
enum class VerificationCodeRequest {
    /**
     * Flag to [RESEND] verification code
     */
    RESEND,

    /**
     * Flag to [VERIFY] verification code
     */
    VERIFY
}

enum class Type { CREATE, UPDATE, DELETE }