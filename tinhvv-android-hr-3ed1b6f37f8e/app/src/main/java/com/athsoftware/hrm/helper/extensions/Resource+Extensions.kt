package com.athsoftware.hrm.helper.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v7.content.res.AppCompatResources
import com.athsoftware.hrm.App
import com.athsoftware.hrm.helper.ColorGenerator
import java.util.*

fun Int.getColor(): Int {
    return ContextCompat.getColor(App.shared(), this)
}

fun Int.getString(): String {
    return App.shared().getString(this)
}

fun Int.getString(param: String): String {
    return App.shared().getString(this, param)
}

fun Int.getStringFormat(vararg param: String): String {
    return String.format(App.shared().getString(this), param)
}

fun Int.getString(map: HashMap<String, String>): String {
    var str = App.shared().getString(this)
    map.iterator().forEach {
        str = str.replace("{" + it.key + "}", it.value)
    }
    return str
}


fun Int.string(): String {
    return App.shared().getString(this)
}

fun randomColor(): Int {
    val red = Random().nextInt(265)
    val green = Random().nextInt(265)
    val blue = Random().nextInt(265)
    return Color.rgb(red, green, blue)
}

fun getColorByString(string: String): Int {
    return ColorGenerator.MATERIAL.getColor(string)
}

fun Int.dp2Px(): Int {
    val m = App.shared().resources.displayMetrics.density
    return (this * m + 0.5f).toInt()
}
fun Int.px(): Int {
    val m = App.shared().resources.displayMetrics.density
    return (this * m + 0.5f).toInt()
}

fun android.support.v4.app.Fragment.dp2Px(dp: Int): Int {
    val m = context?.resources?.displayMetrics?.density ?: return dp
    return (dp * m + 0.5f).toInt()
}

fun Int.getBitmapFromDrawable(): Bitmap {
    val drawable = AppCompatResources.getDrawable(App.shared(), this)

    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }

    if (drawable is VectorDrawableCompat || Build.VERSION.SDK_INT >= Build
                    .VERSION_CODES.LOLLIPOP && drawable is VectorDrawable) {
        val bitmap = Bitmap
                .createBitmap(drawable.intrinsicWidth * 2, drawable.intrinsicHeight * 2,
                        Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    } else {
        throw IllegalArgumentException("unsupported drawable type")
    }
}

fun Float.toOffDay(): String {
    if ((this * 10f % 10).toInt() == 0) {
        return String.format("%.0f", this)
    }
    return String.format("%.1f", this)
}