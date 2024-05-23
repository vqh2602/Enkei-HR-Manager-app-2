package com.athsoftware.hrm.helper.extensions

import android.widget.Toast
import com.athsoftware.hrm.App

/**
 * Created by tranduc on 1/5/18.
 */
fun toast(message: Any, length: Int = Toast.LENGTH_LONG) {
    when (message) {
        is String -> Toast.makeText(App.shared(), message, length).show()
        is Int -> Toast.makeText(App.shared(), message, length).show()
        else -> throw IllegalArgumentException("Argument message type is invalid. The first argument is only accepted on Int or String")
    }
}