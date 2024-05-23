package com.athsoftware.hrm.helper.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.ShareCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.Base64
import android.view.View
import android.widget.TextView
import com.athsoftware.hrm.App
import com.athsoftware.hrm.R
import com.athsoftware.hrm.common.Constants
import com.athsoftware.hrm.helper.AES
import com.athsoftware.hrm.helper.ConvertUnsigned
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


const val REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$"
const val REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$"
const val REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}"
const val REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"
const val REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?"
const val REGEX_USERNAME = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$"
const val REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$"

fun String?.isMobileSimple(): Boolean {
    if (this == null) return false
    return isMatch(REGEX_MOBILE_SIMPLE)
}

fun String?.isMobileExact(): Boolean {
    if (this == null) return false
    return isMatch(REGEX_MOBILE_EXACT)
}

fun String?.isTel(): Boolean {
    if (this == null) return false
    return isMatch(REGEX_TEL)
}

fun String.isEmail(): Boolean {
    return isMatch(REGEX_EMAIL)
}

fun String?.isURL(): Boolean {
    if (this == null) return false
    return isMatch(REGEX_URL)
}

fun String?.isUsername(): Boolean {
    if (this == null) return false
    return isMatch(REGEX_USERNAME)
}

fun String?.isDate(): Boolean {
    if (this == null) return false
    return isMatch(REGEX_DATE)
}

fun String.isMatch(regex: String): Boolean {
    return !this.isEmpty() && Pattern.matches(regex, this)
}

fun String.isEmpty(): Boolean {
    return TextUtils.isEmpty(this)
}

fun String.initialsFromString(): String {
    var string = ""
    val firsts = this.trim().split(" ")
    if (firsts.isNotEmpty()) {
        if (firsts.size == 1) {
            return firsts[0]
        }
        if (firsts.size > 1) {
            return "${firsts[0][0].toUpperCase()}${firsts[firsts.size - 1][0].toUpperCase()}"
        }
    }
    return string
}

fun String.encryptMsg(): String {
    val key = "98302390239430abdc29482934982312312"
    val iv = "83494389819384234324"
    return AES.instance.encrypt(this, key, iv) ?: ""
}

fun String.decryptMsg(): String {
    val key = "98302390239430abdc29482934982312312"
    val iv = "83494389819384234324"
    return AES.instance.decrypt(this, key, iv) ?: ""
}


fun Uri.getRealPath(context: Context): String {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(this, proj, null, null, null)
    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val result = cursor.getString(columnIndex)
    if (cursor != null) {
        cursor.close()
    }
    return result
}


fun TextView.setTextWithSpecialText(text: String, specialText: String, clickable: (() -> Unit)? = null, changeStyle: (TextPaint) -> Unit) {
    if (text.isEmpty() || specialText.isEmpty()) {
        this.text = text
        return
    }
    val startT = text.indexOf(specialText)
    if (startT < 0) {
        this.text = text
        return
    }
    val textSpan = SpannableString(text)
    textSpan.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View?) {
            clickable?.invoke()
        }

        override fun updateDrawState(ds: TextPaint?) {
            super.updateDrawState(ds)
            ds?.let {
                changeStyle.invoke(ds)
            }
        }

    }, startT, startT + specialText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = textSpan
}

fun String.share(subject: String, activity: Activity) {
    ShareCompat.IntentBuilder.from(activity)
            .setType("text/plain")
            .setSubject(subject)
            .setText(this)
            .startChooser()
}


// 1 is newer, 0 is equal, -1 is older
fun versionCompare(str1: String, str2: String): Int {
    val vals1 = str1.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val vals2 = str2.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    var i = 0
    // set index to first non-equal ordinal or length of shortest version string
    while (i < vals1.size && i < vals2.size && vals1[i] == vals2[i]) {
        i++
    }
    // compare first non-equal ordinal number
    if (i < vals1.size && i < vals2.size) {
        val diff = Integer.valueOf(vals1[i])!!.compareTo(Integer.valueOf(vals2[i]))
        return Integer.signum(diff)
    }
    // the strings are equal or one string is a substring of the other
    // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
    return Integer.signum(vals1.size - vals2.size)
}

fun String.toHtml(font: String = "sanspro_regular.ttf", backgroundColor: String = "#F3F3F3"): String {
    val pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/font/$font\")}body {font-family: MyFont;font-size: medium;text-align: justify; padding: 10px;background: $backgroundColor}</style></head><body>"
    val pas = "</body></html>"
    return pish + this + pas
}

fun String.containsIgnoreDiacritic(other: String): Boolean {
    return ConvertUnsigned.get().ConvertString(this).contains(ConvertUnsigned.get().ConvertString(other))
}

fun String.dateConvertFormat(currentFormat: String = Constants.DateFormat.default, showFormat: String = Constants.DateFormat.dateVi): String {
    return try {
        val currentDate = this.toDate(currentFormat)
        val showString = SimpleDateFormat(showFormat, if (App.shared().getCurrentLang() == "en") Locale.ENGLISH else Locale("vi", "vn")).format(currentDate)
        showString
    } catch (e: Exception) {
        this
    }
}

fun String.toDate(currentFormat: String = Constants.DateFormat.default): Date? {
    return try {
        SimpleDateFormat(currentFormat, if (App.shared().getCurrentLang() == "en") Locale.ENGLISH else Locale("vi", "vn")).parse(this)
    } catch (_: Exception) {
        try {
            SimpleDateFormat(Constants.DateFormat.defaultFull, if (App.shared().getCurrentLang() == "en") Locale.ENGLISH else Locale("vi", "vn")).parse(this)
        } catch (_: Exception) {
            try {
                SimpleDateFormat(Constants.DateFormat.default, if (App.shared().getCurrentLang() == "en") Locale.ENGLISH else Locale("vi", "vn")).parse(this)
            } catch (_: Exception) {
                null
            }
        }
    }
}

fun String?.toDateShow(): String? {
    return this?.split(".")?.firstOrNull()?.replace("T", " ")
}

fun String.toImage(): Bitmap? {
    val image = Base64.decode(this, Base64.DEFAULT)
    try {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    } catch (_: java.lang.Exception) {
        return null
    }
}

fun String.toFakeAvatar(): Int {
    when (this.toUpperCase()) {
        "A8C50D11-CAA0-4980-A9F0-485010B99415" -> return R.drawable.ndl
        "39BF116C-6763-4F9C-8C9C-A664D50387BF" -> return R.drawable.lvt
        "886A5B5A-7378-4DD0-AFC8-DE3E1217E9FD" -> return R.drawable.mmc
        "ABA0613B-6F44-4201-A2A9-4B850F5FCE76" -> return R.drawable.ttmh
    }
    return R.drawable.ndl
}

fun String.toCalendar(currentFormat: String = Constants.DateFormat.default): Calendar? {
    val date = try {
        SimpleDateFormat(currentFormat, if (App.shared().getCurrentLang() == "en") Locale.ENGLISH else Locale("vi", "vn")).parse(this)
    } catch (_: Exception) {
        try {
            SimpleDateFormat(Constants.DateFormat.defaultFull, if (App.shared().getCurrentLang() == "en") Locale.ENGLISH else Locale("vi", "vn")).parse(this)
        } catch (_: Exception) {
            try {
                SimpleDateFormat(Constants.DateFormat.default, if (App.shared().getCurrentLang() == "en") Locale.ENGLISH else Locale("vi", "vn")).parse(this)
            } catch (_: Exception) {
                null
            }
        }
    }
    date?.let {
        val calendar = Calendar.getInstance()
        calendar.time = it
        return calendar
    }
    return null
}

fun String.mapError(): String {
    return when(this) {
        "CHANGE_PASSWORD_FAILED" -> R.string.ChangePassFailed.getString()
        "LOGIN_FAILED" -> R.string.loginFailed.getString()
        "ACCOUNT_NOT_EXISTED" -> R.string.ACCOUNT_NOT_EXISTED.getString()
        else -> this
    }
}