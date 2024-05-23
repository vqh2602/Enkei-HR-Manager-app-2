package com.athsoftware.hrm.views.authen

import android.os.Bundle
import com.athsoftware.hrm.App
import com.athsoftware.hrm.MainActivity
import com.athsoftware.hrm.base.BaseActivity
import com.athsoftware.hrm.base.BaseFragment

class LoginActivity : BaseActivity() {

    override fun contentFragment(): BaseFragment? {
        return LoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (App.shared().auth != null) {
            startActivity(MainActivity.newIntent(this, arrayListOf()))
            finish()
            return
        }
        addContentFragmentIfEmpty()
    }

    override fun onBackPressed() {
        finish()
    }
}