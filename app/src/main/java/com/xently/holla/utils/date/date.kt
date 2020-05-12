package com.xently.holla.utils.date

import com.google.firebase.Timestamp
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

/**
 * Contains properties and methods that returns date formatting [String]s
 */
object DateFormat {

    /**
     * Time in hh:mm aa
     */
    const val TIME_HM_12_HRS = "hh:mm aa"

    /**
     * Date in yyyy[ds]MM[ds]dd
     * @param ds Date Separator. Default (-)
     */
    fun dateOnly(ds: Char = '-'): String = "yyyy${ds}MM${ds}dd"
}

@JvmOverloads
fun Timestamp.toPrintableDate(pattern: String = "${DateFormat.dateOnly()} ${DateFormat.TIME_HM_12_HRS}"): String {
    return DateTime(
        toDate().time,
        DateTimeZone.getDefault()
    ).toString(DateTimeFormat.forPattern(pattern))
}