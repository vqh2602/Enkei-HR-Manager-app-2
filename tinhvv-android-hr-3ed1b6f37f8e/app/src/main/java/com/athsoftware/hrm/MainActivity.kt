package com.athsoftware.hrm

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.View
import com.athsoftware.hrm.base.BaseActivity
import com.athsoftware.hrm.base.BaseFragment
import com.athsoftware.hrm.helper.extensions.getColor
import com.athsoftware.hrm.helper.extensions.getString
import com.athsoftware.hrm.helper.extensions.visible
import com.athsoftware.hrm.model.Event
import com.athsoftware.hrm.views.calendar.CalendarFragment
import com.athsoftware.hrm.views.home.HomeFragment
import com.athsoftware.hrm.views.leave.LeaveDetailActivity
import com.athsoftware.hrm.views.notification.NotificationFragment
import com.athsoftware.hrm.views.profile.ProfileFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {

    }

    companion object {
        const val PERMISSIONS = "PERMISSIONS"
        fun newIntent(context: Context?, permisisons: ArrayList<String>): Intent {
            val intent = Intent(context, MainActivity::class.java)
            val bundle = Bundle()
            bundle.putStringArrayList(PERMISSIONS, permisisons)
            intent.putExtras(bundle)
            return intent
        }
    }

    private var isStart = false
    private val homeTab = "TIMELAPSE_ACCESS"
    private val notificationTab = "TRACKING_ACCESS"
    private val calendarTab = "CALENDAR_TAB"
    private val profileTab = "TOTE_ACCESS"
    var disposUploadAvatar: Disposable? = null

    var currentTab = "TIMELAPSE_ACCESS"

    var tab1Fragment: BaseFragment? = null
    var tab2Fragment: BaseFragment? = null
    var tab3Fragment: BaseFragment? = null
    var tab4Fragment: BaseFragment? = null

    override fun contentFragment(): BaseFragment? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        llTote.setOnClickListener(tabClick)
        llMonitoring.setOnClickListener(tabClick)
        llTimelapse.setOnClickListener(tabClick)
        llCalendar.setOnClickListener(tabClick)
        if (savedInstanceState != null) {
            revertFragment(savedInstanceState)
        } else
        setupTab()
        intent?.let {
            if (intent.hasExtra("data")) {
                processNotificationIntent(intent)
            }
        }
    }

    private fun setupTab() {
        switchTab(homeTab)
    }

    override fun onDestroy() {
        disposUploadAvatar?.dispose()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("currentTab", currentTab)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            revertFragment(savedInstanceState)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    fun revertFragment(state: Bundle) {
        currentTab = state.getString("currentTab")
        tab1Fragment = supportFragmentManager.findFragmentByTag(homeTab) as BaseFragment?
        tab2Fragment = supportFragmentManager
                .findFragmentByTag(notificationTab) as BaseFragment?
        tab3Fragment = supportFragmentManager.findFragmentByTag(notificationTab) as BaseFragment?
        tab4Fragment = supportFragmentManager.findFragmentByTag(profileTab) as BaseFragment?
        switchTab(currentTab)
    }

    private val tabClick = View.OnClickListener { view ->
        when (view.id) {
            R.id.llTimelapse -> {
                switchTab(homeTab)
            }
            R.id.llMonitoring -> {
                switchTab(notificationTab)
            }
            R.id.llCalendar -> {
                switchTab(calendarTab)
            }
            R.id.llTote -> {
                switchTab(profileTab)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        tab1Fragment?.onPause()
        tab2Fragment?.onPause()
        tab3Fragment?.onPause()
        tab4Fragment?.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (isStart)
        reloadTab(currentTab)
        isStart = true
    }

    fun switchTab(tab: String) {
        reloadTab(tab)
        val transaction = supportFragmentManager.beginTransaction()
        var fragment = supportFragmentManager.findFragmentByTag(tab) as? BaseFragment
        var isCreated = true
        when (tab) {
            homeTab -> {
                if (tab1Fragment == null) {
                    tab1Fragment = HomeFragment()
                    isCreated = false
                }
                fragment = tab1Fragment
                changeTitle(R.string.Home_Title.getString())
            }
            notificationTab -> {
                if (tab2Fragment == null) {
                    tab2Fragment = NotificationFragment()
                    isCreated = false
                }
                fragment = tab2Fragment
                changeTitle(R.string.Notification_Title.getString())
            }
            calendarTab -> {
                if (tab3Fragment == null) {
                    tab3Fragment = CalendarFragment()
                    isCreated = false
                }
                fragment = tab3Fragment
                changeTitle(R.string.calendar.getString())
            }
            profileTab -> {
                if (tab4Fragment == null) {
                    tab4Fragment = ProfileFragment()
                    isCreated = false
                }
                fragment = tab4Fragment
                changeTitle("", true)
            }
        }
        showRightMentu(tab == homeTab)
        if (fragment?.isAdded != true) {
            transaction.add(R.id.flLayout, fragment, tab)
        } else {
            transaction.show(fragment)
        }
        currentTab = tab
        setTabColor(tab)
        transaction.commitAllowingStateLoss()
        hiddenAllFragmentNotShow(transaction)
//        if (isCreated) {
//            fragment?.onRefresh()
//        }
    }

    private fun reloadTab(tab: String) {
//        when (tab) {
//            homeTab -> {
//                tab1Fragment?.onRefresh()
//            }
//            notificationTab -> {
//                tab2Fragment?.onRefresh()
//            }
//            profileTab -> {
//                tab3Fragment?.onRefresh()
//            }
//        }
    }

    private fun setTabColor(tab: String) {
        initAllTabColor()
        when (tab) {
            homeTab -> {
                tvTimelapse.setTextColor(R.color.colorPrimary.getColor())
                ivTimelapse.imageTintList = ColorStateList.valueOf(R.color.colorPrimary.getColor())
            }
            notificationTab -> {
                tvMonitoring.setTextColor(R.color.colorPrimary.getColor())
                ivMonitoring.imageTintList = ColorStateList.valueOf(R.color.colorPrimary.getColor())
            }
            calendarTab -> {
                tvCalendar.setTextColor(R.color.colorPrimary.getColor())
                ivCalendar.imageTintList = ColorStateList.valueOf(R.color.colorPrimary.getColor())
            }
            profileTab -> {
                tvTote.setTextColor(R.color.colorPrimary.getColor())
                ivTote.imageTintList = ColorStateList.valueOf(R.color.colorPrimary.getColor())
            }
        }
    }

    private fun initAllTabColor() {
        tvTimelapse.setTextColor(R.color.text888686.getColor())
        ivTimelapse.imageTintList = ColorStateList.valueOf(R.color.text888686.getColor())
        tvMonitoring.setTextColor(R.color.text888686.getColor())
        ivMonitoring.imageTintList = ColorStateList.valueOf(R.color.text888686.getColor())
        tvCalendar.setTextColor(R.color.text888686.getColor())
        ivCalendar.imageTintList = ColorStateList.valueOf(R.color.text888686.getColor())
        tvTote.setTextColor(R.color.text888686.getColor())
        ivTote.imageTintList = ColorStateList.valueOf(R.color.text888686.getColor())
    }

    private fun hiddenAllFragmentNotShow(fragmentTransaction: FragmentTransaction) {
        if (currentTab != homeTab) {
            tab1Fragment?.let {
                fragmentTransaction.hide(it)
            }
        }
        if (currentTab != notificationTab) {
            tab2Fragment?.let {
                fragmentTransaction.hide(it)
            }
        }
        if (currentTab != calendarTab) {
            tab3Fragment?.let {
                fragmentTransaction.hide(it)
            }
        }
        if (currentTab != profileTab) {
            tab4Fragment?.let {
                fragmentTransaction.hide(it)
            }
        }
    }

    fun changeTitle(title: String? = R.string.app_namecom_space.getString(), isHidden: Boolean = false) {
        tvTitle.text = title
        actionBarLl.visible(!isHidden)
    }
    fun showRightMentu(isShow: Boolean = true) {
        btnMenuRight.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
    }

    fun setOnRightClickListener(listener: () -> Unit) {
        btnMenuRight.setOnClickListener {
            listener.invoke()
        }
    }

    fun backFragment() {
        val isBackStack = when (currentTab) {
            homeTab -> {
                tab1Fragment?.backStack()
            }
            notificationTab -> {
                tab2Fragment?.backStack()
            }
            calendarTab -> {
                tab3Fragment?.backStack()
            }
            profileTab -> {
                tab4Fragment?.backStack()
            }
            else -> false
        } ?: false
        if (!isBackStack) {
            supportFragmentManager?.popBackStack()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if (intent.hasExtra("data")) {
                processNotificationIntent(intent)
            }
        }
    }

    private fun processNotificationIntent(intent: Intent) {
        val data = intent.getStringExtra("data")
        val mapData = Gson().fromJson<Map<String, Any>>(data, object : TypeToken<Map<String, Any>>() {}.type)
        if (mapData.containsKey("id")) {
            actionBarLl.postDelayed({
                val id = (mapData["id"] as? String) ?: return@postDelayed
                if (id.isNotEmpty()) {
                    val notify = Event()
                    notify.registerId = id
                    val intentDetail = LeaveDetailActivity.getIntent(this@MainActivity, notify)
                    this@MainActivity.startActivity(intentDetail)
                }
            }, 1000)
        }
        this@MainActivity.intent = null
    }

}
