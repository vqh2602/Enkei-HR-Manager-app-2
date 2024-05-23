package com.athsoftware.hrm.helper.extensions

import com.athsoftware.hrm.App
import com.athsoftware.hrm.R
import com.athsoftware.hrm.common.Constants
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toStringFormat(format: String = Constants.DateFormat.dateVi): String? {
    return try {
        SimpleDateFormat(format, if (App.shared().getCurrentLang() == "en") Locale.ENGLISH else Locale("vi", "vn")).format(time)
    } catch (_: Exception) {
        null
    }
}

fun Date.toStringFormat(format: String = Constants.DateFormat.defaultFull): String? {
    return try {
        SimpleDateFormat(format, if (App.shared().getCurrentLang() == "en") Locale.ENGLISH else Locale("vi", "vn")).format(this)
    } catch (_: Exception) {
        null
    }
}

fun Calendar.year(): Int {
    return this.get(Calendar.YEAR)
}

fun Calendar.month(): Int {
    return this.get(Calendar.MONTH)
}

fun Calendar.day(): Int {
    return this.get(Calendar.DAY_OF_MONTH)
}

fun Calendar.resetTime() {
    this.set(Calendar.MILLISECOND, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.HOUR_OF_DAY, 0)
}

fun Calendar.isSame(compair: Calendar): Boolean {
    return this.toStringFormat() == compair.toStringFormat()
}

fun Long.minutesToAgo(): String {
    val today = Date().time
    if (today < this) return ""
    val minutes = (today - this) / (1000 * 60)
    val hour = minutes / 60
    val day = hour / 24
    val month = day / 30
    val year = month / 12
    if (year > 0) {
        return "$year ${R.string.YearAgo.getString()}"
    }
    if (month > 0) {
        return "$month ${R.string.MonthAgo.getString()}"
    }
    if (day > 0) {
        return "$day ${R.string.DayAgo.getString()}"
    }
    if (hour > 0) {
        return "$hour ${R.string.HourAgo.getString()}"
    }
    return "$minutes ${R.string.MinAgo.getString()}"
}

fun Calendar.toCalendar(): com.haibin.calendarview.Calendar {
    val cal = com.haibin.calendarview.Calendar()
    cal.day = this.get(Calendar.DAY_OF_MONTH)
    cal.month = this.get(Calendar.MONTH) + 1
    cal.year = this.get(Calendar.YEAR)
    return cal
}