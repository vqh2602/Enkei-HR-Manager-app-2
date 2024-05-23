package com.athsoftware.hrm.base

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.LifecycleObserver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import com.athsoftware.hrm.R
import com.athsoftware.hrm.helper.LocaleHelper
import com.athsoftware.hrm.helper.extensions.bind
import com.athsoftware.hrm.helper.extensions.getColor
import java.lang.ref.WeakReference

abstract class BaseActivity : AppCompatActivity(){
    protected val REQUEST_CHECK_SETTINGS = 127

    companion object {
        val TAG = "BaseActivity"
    }

    open fun contentFragment(): BaseFragment? = null
    open fun isUsingBaseContent() = true
    public open val isStatusLight = true

    open fun addContentFragmentIfEmpty() {
        Log.d(TAG, "addContentFragmentIfEmpty ")
        var fragment = supportFragmentManager.findFragmentById(R.id.contentLayout)
        if (fragment != null) {
            return
        }
        fragment = contentFragment()
        if (fragment == null) {
            return
        }

        Log.d(TAG, "addContentFragmentIfEmpty begin")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.contentLayout, fragment)
        transaction.addToBackStack("Root")
        transaction.commit()

        Log.d(TAG, "addContentFragmentIfEmpty done")
    }

    val listenerLocationChange: ArrayList<WeakReference<(Location?) -> Unit>> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isUsingBaseContent()) {
            setContentView(R.layout.activity_base)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isStatusLight) {
            setLightStatusBar()
        } else {
            clearLightStatusBar()
        }
    }

    open fun openFragment(fragment: BaseFragment, addToBackStack: Boolean = true, name: String? = null) {
        if (bind<View>(R.id.contentLayout) != null) {
            val transaction = supportFragmentManager
                    .beginTransaction()
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right)
            transaction.add(R.id.contentLayout, fragment)
            if (addToBackStack) {
                transaction.addToBackStack(name)
            }
            transaction.commitAllowingStateLoss()
        }
    }

    fun popBackStack(name: String, flags: Int) {
        supportFragmentManager.popBackStack(name, flags)
    }

    fun popBackStackImmediate(name: String, flags: Int): Boolean {
        return supportFragmentManager.popBackStackImmediate(name, flags)
    }


    fun showConfirm(title: String? = null, content: String, rightButtonTitle: String = "Đồng ý", handlerRight: ((DialogInterface) -> Unit)? = null, leftButtonTitle: String? = null, handlerLeft: ((DialogInterface) -> Unit)? = null) {
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setPositiveButton(rightButtonTitle, null)
        title?.let {
            dialog.setTitle(title)
        }
        leftButtonTitle?.let {
            dialog.setNegativeButton(it, null)
        }
        dialog.setMessage(content)
        val diaInterface = dialog.create()
        diaInterface.setOnShowListener { diai ->
            val positive = diaInterface.getButton(AlertDialog.BUTTON_POSITIVE)
            positive?.setOnClickListener {
                handlerRight?.invoke(diai)
            }

            val negative = diaInterface.getButton(AlertDialog.BUTTON_NEGATIVE)
            negative?.setOnClickListener {
                handlerLeft?.invoke(diai)
            }
        }
        diaInterface.show()
    }

    fun showMessage(content: String, leftButtonTitle: String = "OK") {
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        leftButtonTitle?.let {
            dialog.setNegativeButton(it, null)
        }
        dialog.setMessage(content)
        val diaInterface = dialog.create()
        diaInterface.show()
    }


    fun setLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    fun clearLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            window.decorView.systemUiVisibility = flags
            window.statusBarColor = R.color.colorPrimary.getColor()
        }
    }
}