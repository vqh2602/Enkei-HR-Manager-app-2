package com.athsoftware.hrm.views.leave

import android.os.Bundle
import com.athsoftware.hrm.base.BaseActivity
import com.athsoftware.hrm.base.BaseFragment

class CreateLeaveActivity : BaseActivity() {

    private var fragment: CreateLeaveFragment? = null
    override val isStatusLight = false

    override fun contentFragment(): BaseFragment? {
        return fragment
    }

    private var isAdd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragment = CreateLeaveFragment.getInstance(intent.extras ?: Bundle())
        addContentFragmentIfEmpty()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAdd)
            fragment?.onResume()
        isAdd = true
    }
}