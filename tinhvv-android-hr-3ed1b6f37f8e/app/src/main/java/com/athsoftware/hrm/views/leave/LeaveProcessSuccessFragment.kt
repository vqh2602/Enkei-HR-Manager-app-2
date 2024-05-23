package com.athsoftware.hrm.views.leave

import android.os.Bundle
import android.view.View
import com.athsoftware.hrm.R
import com.athsoftware.hrm.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_leave_process_success.*

class LeaveProcessSuccessFragment: BaseFragment() {

    companion object {
        fun getInstance(homeListener: () -> Unit): LeaveProcessSuccessFragment {
            val frag = LeaveProcessSuccessFragment()
            frag.homeListener = homeListener
            return frag
        }
    }

    private lateinit var homeListener: () -> Unit

    override fun getLayoutRes(): Int {
        return R.layout.fragment_leave_process_success
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnHome.setOnClickListener {
            homeListener()
            back()
        }
    }
}