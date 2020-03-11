package com.xently.holla.utils.date

import android.content.Context
import com.xently.holla.R
import com.xently.holla.utils.DurationUnit
import com.xently.holla.utils.DurationUnit.*
import org.joda.time.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.PeriodFormat
import java.util.*

/**
 * Contains properties and methods that returns date formatting [String]s
 */
object DateFormat {
    const val LONG_FOR_SORTING: String = "yyyyMMddHHmmss"

    const val DOCUMENT_DATE_TIME: String = "${LONG_FOR_SORTING}SSSSSS"

    /**
     * Time in HH:mm
     */
    const val TIME_HM_24_HRS = "HH:mm"

    /**
     * Time in hh:mm aa
     */
    const val TIME_HM_12_HRS = "hh:mm aa"

    /**
     * Time in hh:mm
     */
    const val TIME_HM_12_HRS_NO_AM_PM = "hh:mm"

    /**
     * Time in HH:mm:ss
     */
    const val TIME_HMS_24_HRS = "HH:mm:ss"

    /**
     * Time in hh:mm:ss aa
     */
    const val TIME_HMS_12_HRS = "hh:mm:ss aa"

    /**
     * Time in hh:mm:ss
     */
    const val TIME_HMS_12_HRS_NO_AM_PM = "hh:mm:ss"

    val STANDARD_DATE_TIME = "${dateOnly()} $TIME_HM_24_HRS"

    val STANDARD_DATE_TIME_WITH_TZ = "${STANDARD_DATE_TIME}ZZ"

    const val ISO_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZ"

    /**
     * @param ds Date Separator. Default (-)
     * @param ts Time Separator Default (:)
     */
    fun isoWithoutMillis(ds: Char = '-', ts: Char = ':'): String =
        "yyyy${ds}MM${ds}dd'T'HH${ts}mm${ts}ssZZ"

    /**
     * @param ds Date Separator. Default (-)
     * @param ts Time Separator Default (:)
     */
    fun longWithOutTimeZone(ds: Char = '-', ts: Char = ':'): String =
        "yyyy${ds}MM${ds}dd HH${ts}mm${ts}ss"

    /**
     * @param ds Date Separator. Default (-)
     * @param ts Time Separator Default (:)
     */
    fun longWithTimeZone(ds: Char = '-', ts: Char = ':'): String =
        "${longWithOutTimeZone(ds, ts)}ZZ"

    /**
     * Date in yyyy[ds]MM[ds]dd
     * @param ds Date Separator. Default (-)
     */
    fun dateOnly(ds: Char = '-'): String = "yyyy${ds}MM${ds}dd"
}

fun printableCurrentDate(format: String = DateFormat.dateOnly()): String =
    DateTime.now().toString(format)

fun printableCurrentTime(timeFormat: String = DateFormat.TIME_HM_24_HRS): String =
    DateTime.now().toString(timeFormat)

fun printableCurrentDateTime(format: String = "yyyy-MM-dd HH:mm"): String =
    DateTime.now().toString(format)

/**
 * Combines [date] and [time] to produce [DateTime] equivalent
 * @param dateFormat pattern/format of [date]
 * @param timeFormat pattern/format of [time]
 * @throws IllegalFieldValueException if either [date] or [time] does not correspond to their
 * [dateFormat] or [timeFormat] respectively
 */
@Throws(IllegalFieldValueException::class)
fun combineToDateTime(
    date: CharSequence,
    time: CharSequence?,
    dateFormat: String = DateFormat.dateOnly(),
    timeFormat: String = DateFormat.TIME_HM_24_HRS
): DateTime = DateTime.parse(
    "$date $time", DateTimeFormat.forPattern("$dateFormat $timeFormat")
        .withZone(DateTimeZone.getDefault())
        .withLocale(Locale.getDefault())
)

/**
 * @param resultFormat pattern/format of resulting date and time
 * @return Combination of [date] and [time] in [resultFormat] date and time pattern/format
 * @see combineToDateTime
 */
@Throws(IllegalFieldValueException::class)
fun combineToPrintableDateTime(
    date: CharSequence,
    time: CharSequence?,
    resultFormat: DateTimeFormatter,
    dateFormat: String = DateFormat.dateOnly(),
    timeFormat: String = DateFormat.TIME_HM_24_HRS
): String = combineToDateTime(
    date,
    time,
    dateFormat,
    timeFormat
)
    .toString(resultFormat.withLocale(Locale.US))

/**
 * @see combineToPrintableDateTime
 */
fun combineToPrintableDateTime(
    date: CharSequence,
    time: CharSequence?,
    dateFormat: String = DateFormat.dateOnly(),
    timeFormat: String = DateFormat.TIME_HM_24_HRS,
    resultFormat: String = DateFormat.STANDARD_DATE_TIME_WITH_TZ,
    zone: DateTimeZone = DateTimeZone.UTC
): String = combineToPrintableDateTime(
    date,
    time,
    DateTimeFormat.forPattern(resultFormat).withZone(zone).withLocale(Locale.US),
    dateFormat,
    timeFormat
)

/**
 * Does the opposite of [combineToDateTime]
 * @param sourceFormat pattern/format of [this@separateToPrintableDateTimePair] date-time
 * @param dateFormat pattern/format of returned date
 * @param timeFormat pattern/format of returned time
 * @return Pair(Date, Time)
 * @see combineToDateTime
 * @throws IllegalArgumentException
 */
@Throws(IllegalArgumentException::class, IllegalFieldValueException::class)
fun CharSequence.separateToPrintableDateTimePair(
    sourceFormat: String = DateFormat.isoWithoutMillis(),
    dateFormat: String = DateFormat.dateOnly(),
    timeFormat: String = DateFormat.TIME_HM_24_HRS
): Pair<String, String> =
    this.separateToPrintableDateTimePair(
        DateTimeFormat.forPattern(sourceFormat),
        dateFormat,
        timeFormat
    )

/**
 * @see separateToPrintableDateTimePair
 */
fun CharSequence.separateToPrintableDateTimePair(
    sourceFormat: DateTimeFormatter,
    dateFormat: String = DateFormat.dateOnly(),
    timeFormat: String = DateFormat.TIME_HM_24_HRS
): Pair<String, String> {
    val dt = toDateTime(sourceFormat)
    return Pair(
        dt.toString(DateTimeFormat.forPattern(dateFormat).withZone(DateTimeZone.getDefault())),
        dt.toString(DateTimeFormat.forPattern(timeFormat).withZone(DateTimeZone.getDefault()))
    )
}

/**
 * Converts a date, time or date and time string to [DateTime] object with desired
 * [Locale] and [DateTimeZone]
 * @param sourceFormat date sourceFormat from source(@receiver)
 * @param locale defaults to [Locale.getDefault]
 * @param timeZone defaults to [DateTimeZone.getDefault]
 * @return [DateTime]
 */
fun CharSequence.toDateTime(
    sourceFormat: String = DateFormat.isoWithoutMillis(),
    locale: Locale = Locale.getDefault(),
    timeZone: DateTimeZone = DateTimeZone.getDefault()
): DateTime {
    val dateFormat = DateTimeFormat.forPattern(sourceFormat)
        .withZone(timeZone)
        .withLocale(locale)

    return this@toDateTime.toDateTime(dateFormat)
}

/**
 * @see toDateTime
 */
fun CharSequence.toDateTime(sourceFormat: DateTimeFormatter): DateTime =
    sourceFormat.parseDateTime(this@toDateTime.toString())

/**
 * Returns a [String] of a formatted [this@printableDate], time or date and time
 * @param this@printableDate date whose [sourceDateFormat] should be changed to [targetDateFormat]
 * @param sourceDateFormat for [this@printableDate]
 * @param targetDateFormat to be used to when formatting [this@printableDate] date, time or date and time
 * produced as output
 * @return [String]
 */
fun CharSequence?.toPrintableDate(
    sourceDateFormat: String = DateFormat.isoWithoutMillis(),
    targetDateFormat: String = DateFormat.dateOnly()
): String? = if (!this.isNullOrBlank()) {
    toDateTime(sourceFormat = sourceDateFormat).toString(targetDateFormat)
} else null

/**
 * @see toPrintableDate
 * @see durationToGo
 */
fun CharSequence?.toPrintableDateDurationLeft(
    context: Context?,
    sourceDateFormat: String = DateFormat.isoWithoutMillis(),
    defaultResponse: String = context?.getString(R.string.date_unspecified) ?: "Unspecified",
    futureSuffix: String = context?.getString(R.string.left) ?: "left",
    pastSuffix: String = context?.getString(R.string.ago) ?: "ago",
    shortenDurationUnits: Boolean = true
): String {
    if (this.isNullOrBlank()) return defaultResponse

    val currentDate = DateTime.now()
    val futureDate = toDateTime(sourceDateFormat)

    // Use [time-ago] for [payDate] lower than [currentDate] and do the reverse in the
    // opposite situation
    var msg = futureSuffix
    val interval = if (currentDate.millis <= futureDate.millis) {
        Interval(currentDate, futureDate)
    } else {
        msg = pastSuffix
        Interval(futureDate, currentDate)
    }

    return "${toPrintableDate(sourceDateFormat = sourceDateFormat)
        ?: defaultResponse}, ${interval.toPeriod().durationToGo(
        context,
        shortenDurationUnits
    )} $msg"
}

/**
 * @receiver this@durationLeft - the future or past date from which duration left/past is to be
 * calculated
 * @throws IllegalArgumentException if [sourceDateFormat] not correspond to date and/or date-time
 * provided by @receiver
 */
@Throws(IllegalArgumentException::class)
fun CharSequence?.durationLeft(
    duration: DurationUnit = DAYS,
    sourceDateFormat: String = DateFormat.isoWithoutMillis()
): Int {
    if (this == null) return 0
    val currentDate = DateTime.now()
    val futureDate = toDateTime(sourceDateFormat)

    val interval = if (currentDate.millis <= futureDate.millis) {
        Interval(currentDate, futureDate)
    } else {
        Interval(futureDate, currentDate)
    }
    val period = interval.toPeriod()

    return when (duration) {
        MILLISECONDS -> interval.toDurationMillis().toInt()
        SECONDS -> Seconds.standardSecondsIn(period).seconds
        MINUTES -> Minutes.standardMinutesIn(period).minutes
        HOURS -> Hours.standardHoursIn(period).hours
        DAYS -> Days.standardDaysIn(period).days
        WEEKS -> Weeks.standardWeeksIn(period).weeks
        MONTHS -> Months.monthsIn(interval).months
        YEARS -> Years.yearsIn(interval).years
    }
}

fun String?.toFilterableMillis(
    sourceFormat: String = DateFormat.isoWithoutMillis(),
    resultFormat: String = DateFormat.dateOnly()
): Long? = this?.toDateTime(sourceFormat)?.toString(resultFormat)?.toDateTime(resultFormat)?.millis

/**
 * Extract and returns the first two words of a [Period] e.g. if a [Period] prints
 * 1 year, 8 months and 12 weeks then this will return 1 year and 8 months
 * year = yr
 * month = mnt
 * week = wk
 * day = day
 * hour = hr
 * minutes = min
 * seconds = sec
 * milliseconds = ms
 * @param shortenDurationUnits
 * @receiver [Period]
 * @return [String] of a two-word duration remaining
 */
@Suppress("LocalVariableName")
fun Period.durationToGo(context: Context?, shortenDurationUnits: Boolean = true): String {
    val and = context?.getString(R.string.and) ?: "and"
    val year = context?.getString(R.string.year) ?: "year"
    val month = context?.getString(R.string.month) ?: "month"
    val week = context?.getString(R.string.week) ?: "week"
    val hour = context?.getString(R.string.hour) ?: "hour"
    val minute = context?.getString(R.string.minute) ?: "minute"
    val second = context?.getString(R.string.second) ?: "second"
    val millisecond = context?.getString(R.string.millisecond) ?: "millisecond"

    val year_abbrev = context?.getString(R.string.year_abbrev) ?: "yr"
    val month_abbrev = context?.getString(R.string.month_abbrev) ?: "mon"
    val week_abbrev = context?.getString(R.string.week_abbrev) ?: "wk"
    val hour_abbrev = context?.getString(R.string.hour_abbrev) ?: "hr"
    val minute_abbrev = context?.getString(R.string.minute_abbrev) ?: "min"
    val second_abbrev = context?.getString(R.string.second_abbrev) ?: "sec"
    val millisecond_abbrev = context?.getString(R.string.millisecond_abbrev) ?: "ms"

    val dAgo =
        this.toString(PeriodFormat.wordBased().withLocale(Locale.getDefault()))
    val dAgoList = when {
        dAgo.contains(',') -> dAgo.split(',')
        dAgo.contains(and, true) || dAgo.contains(and) -> dAgo.split(and)
        else -> listOf(dAgo)
    }

    val res = "${dAgoList.component1()} &${dAgoList.component2()}"
    return if (shortenDurationUnits) {
        res.replace(" $year", year_abbrev)
            .replace(" $month", month_abbrev)
            .replace(" $week", week_abbrev)
            .replace(" $hour", hour_abbrev)
            .replace(" $minute", minute_abbrev)
            .replace(" $second", second_abbrev)
            .replace(" $millisecond", millisecond_abbrev)
    } else res
}