package com.athsoftware.hrm.helper.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by vinh on 11/9/17.
 */

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

val ViewGroup.children: List<View>
    get() = (0 until childCount).map { getChildAt(it) }

operator fun ViewGroup.get(i: Int): View? = getChildAt(i)

/**
 * -=
 */
operator fun ViewGroup.minusAssign(child: View) = removeView(child)

/**
 * +=
 */
operator fun ViewGroup.plusAssign(child: View) = addView(child)

/**
 * if (view in views)
 */
fun ViewGroup.contains(child: View) = indexOfChild(child) != -1

fun ViewGroup.first(): View? = this[0]

fun ViewGroup.forEach(action: (View) -> Unit) {
    for (i in 0 until childCount) {
        action(getChildAt(i))
    }
}

fun ViewGroup.forEachIndexed(action: (Int, View) -> Unit) {
    for (i in 0 until childCount) {
        action(i, getChildAt(i))
    }
}