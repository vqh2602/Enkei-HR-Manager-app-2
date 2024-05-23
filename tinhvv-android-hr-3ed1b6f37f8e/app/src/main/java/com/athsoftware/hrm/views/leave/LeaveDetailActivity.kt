package com.athsoftware.hrm.views.leave

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.athsoftware.hrm.base.BaseActivity
import com.athsoftware.hrm.base.BaseFragment
import com.athsoftware.hrm.model.Event

class LeaveDetailActivity : BaseActivity() {

    companion object {

        const val KEY_EVENT = "event"
        const val KEY_IS_DELETE = "isDelete"
        const val KEY_EVENT_ID = "event_id"

        fun getIntent(context: Context, event: Event): Intent {
            val intent = Intent(context, LeaveDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(KEY_EVENT, event)
            intent.putExtras(bundle)
            return intent
        }
    }

    private lateinit var fragment: LeaveDetailFragment

    override fun contentFragment(): BaseFragment? {
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val event = intent.getSerializableExtra(KEY_EVENT) as Event
        fragment = LeaveDetailFragment.getInstance(event)
        addContentFragmentIfEmpty()
    }

    override fun onBackPressed() {
        finish()
    }
}