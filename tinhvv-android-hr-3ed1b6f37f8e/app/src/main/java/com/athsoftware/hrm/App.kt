package com.athsoftware.hrm

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.athsoftware.hrm.common.Constants
import com.athsoftware.hrm.fcm.HRMFCMInstanceMessage
import com.athsoftware.hrm.helper.LocaleHelper
import com.athsoftware.hrm.helper.extensions.PreferencesHelper
import com.athsoftware.hrm.helper.extensions.applyOn
import com.athsoftware.hrm.helper.extensions.toCalendar
import com.athsoftware.hrm.helper.extensions.toStringFormat
import com.athsoftware.hrm.model.Event
import com.athsoftware.hrm.model.Login
import com.athsoftware.hrm.network.APIClient
import com.athsoftware.hrm.views.authen.LoginActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference
import java.util.*

class App : MultiDexApplication() {

    var auth: Login? = null
    var imageCache: HashMap<String?, String?> = hashMapOf()
    var imageCacheListener: HashMap<String, MutableList<WeakReference<(String, Int) -> Unit>>> = hashMapOf()
    var dataDayLeave: HashMap<String, MutableList<com.haibin.calendarview.Calendar>> = hashMapOf()
    private var listEvent: List<Event>? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val data = PreferencesHelper.shared.getStringValue(Constants.Key.user, "") ?: ""
        auth = if (data.isEmpty()) {
            null
        } else {
            Gson().fromJson(data, Login::class.java)
        }
        registerDeviceToken()
    }

    fun loadAllEvents(listener: ((List<Event>) -> Unit)? = null) {
        if (listEvent != null && listener != null) {
            listener(listEvent!!)
            return
        }
        val userId = App.userId ?: return
        val dayFilter: HashMap<String, Any> = hashMapOf("LoginUserId" to userId)
        val request = APIClient.shared.api.getCalendarEvents(dayFilter).applyOn()
                .subscribe({
                    listEvent = it.data ?: listOf()
                    listener?.invoke(listEvent!!)
                }, {
                    listener?.invoke(listEvent ?: listOf())
                })
    }

    fun loadLeaveDay(year: String, listener: ((MutableList<com.haibin.calendarview.Calendar>) -> Unit)?): Disposable? {
        if (dataDayLeave[year].isNullOrEmpty()) {
            return APIClient.shared.api.getLeaveTime(year)
                    .map { response ->
                        val listCalendar = mutableListOf<com.haibin.calendarview.Calendar>()
                        response.data?.forEach {
                            it.toCalendar()?.let { calendar ->
                                listCalendar.add(calendar.toCalendar())
                            }
                        }
                        dataDayLeave[year] = listCalendar
                        return@map listCalendar
                    }
                    .applyOn()
                    .subscribe({
                        listener?.invoke(it)
                    }, {
                        listener?.invoke(mutableListOf())
                    })
        }
        listener?.invoke(dataDayLeave[year]!!)
        return null
    }

    fun restartApp() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        System.exit(0)
    }

    fun changeLanguage(lang: String = Constants.Lang.vi) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        instance.baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        instance.resources.updateConfiguration(config, instance.resources.displayMetrics)
        PreferencesHelper.shared.putValue(Constants.Key.language, lang)
    }

    fun getCurrentLang(): String {
//        return PreferencesHelper.shared.getStringValue(Constant.Key.language, Locale.getDefault().language) ?: Locale.getDefault().language
        return LocaleHelper.getLanguage(this)
    }

    fun isLangVi(): Boolean {
        return getCurrentLang() != "en"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {

        private lateinit var instance: App
        var userId: String? = null
            get() {
                return instance.auth?.userId
            }

        fun shared(): App {
            return instance
        }

        fun registerDeviceToken() {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                HRMFCMInstanceMessage.registerToServer(it.token)
            }
        }

        fun unregisterDeviceToken() {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                HRMFCMInstanceMessage.unregisterToServer(it.token)
            }
        }
    }
}