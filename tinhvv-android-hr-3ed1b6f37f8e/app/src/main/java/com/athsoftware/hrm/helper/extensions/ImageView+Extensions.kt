package com.athsoftware.hrm.helper.extensions

import android.util.Base64
import android.widget.ImageView
import com.athsoftware.hrm.helper.support.TextDrawable
import com.athsoftware.hrm.network.GlideApp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * Created by tinhvv on 11/14/18.
 */

fun ImageView.load(id: Int) {
    Glide.with(context).load(id).into(this)
}
fun ImageView.load(url: String?) {
    if (url == null) return
    if (url.isEmpty()) return
    GlideApp.with(context).load(url)
            .into(this)
}

fun ImageView.loadBase64(image: String) {
    val imageByte = Base64.decode(image, Base64.DEFAULT)
    GlideApp.with(context).load(imageByte)
            .into(this)

}

fun ImageView.setImageForName(string: String, backgroundColor: Int? = null, circuler: Boolean = true, customWidth: Int? = null, customHeight: Int? = null) {
    val builder = TextDrawable.builder().beginConfig()
            .height(if (height > 0) height else customWidth ?: 100)
            .width(if (width > 0) width else customHeight ?: 100)
            .endConfig()
    var color = backgroundColor
    val text = string.initialsFromString()
    if (color == null) {
        color = getColorByString(text)
    }
    if (circuler) {
        setImageDrawable(builder.buildRound(text, color))
    } else {
        setImageDrawable(builder.buildRect(text, color))
    }
}
