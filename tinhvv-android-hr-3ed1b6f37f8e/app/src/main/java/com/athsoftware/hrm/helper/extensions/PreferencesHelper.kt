package com.athsoftware.hrm.helper.extensions

import android.content.Context
import android.content.SharedPreferences
import com.athsoftware.hrm.App

class PreferencesHelper(fileName: String = "beta_pref_file") {

    private val mPref: SharedPreferences = App.shared().getSharedPreferences(fileName, Context.MODE_PRIVATE)

    fun clear() {
        mPref.edit().clear().apply()
    }

    fun putValue(key: String, value: Any) {
        when (value) {
            is String -> mPref.edit().putString(key.encryptMsg(), value.encryptMsg()).apply()
            is Int -> mPref.edit().putInt(key.encryptMsg(), value).apply()
            is Boolean -> mPref.edit().putBoolean(key.encryptMsg(), value).apply()
            is Float -> mPref.edit().putFloat(key.encryptMsg(), value).apply()
            else -> Throwable("Put Object not support")
        }
    }

    fun getStringValue(key: String, default: String? = null): String? {
        return mPref.getString(key.encryptMsg(), default)?.decryptMsg()
    }

    fun getIntValue(key: String, default: Int = 0): Int? {
        return mPref.getInt(key.encryptMsg(), default)
    }

    fun getFloatValue(key: String, default: Float = 0f): Float? {
        return mPref.getFloat(key.encryptMsg(), default)
    }

    fun getBooleanValue(key: String, default: Boolean = false): Boolean {
        return mPref.getBoolean(key.encryptMsg(), default)
    }

    fun remove(key: String) {
        mPref.edit().remove(key.encryptMsg()).apply()
    }

    companion object {
        val shared = PreferencesHelper()
        val PREF_FILE_NAME = "beta_pref_file"
    }

}