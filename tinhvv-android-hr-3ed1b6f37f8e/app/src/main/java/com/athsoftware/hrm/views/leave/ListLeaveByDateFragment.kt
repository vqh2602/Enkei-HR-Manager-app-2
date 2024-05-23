package com.athsoftware.hrm.views.leave

import android.os.Bundle
import android.view.View
import com.athsoftware.hrm.App
import com.athsoftware.hrm.R
import com.athsoftware.hrm.base.BaseFragment
import com.athsoftware.hrm.helper.extensions.applyOn
import com.athsoftware.hrm.helper.extensions.toStringFormat
import com.athsoftware.hrm.model.Event
import com.athsoftware.hrm.network.APIClient
import java.lang.ref.WeakReference
import java.util.*

class ListLeaveByDateFragment : BaseFragment() {

    lateinit var calendar: Calendar

    companion object {
        fun getInstance(calendar: Calendar): ListLeaveByDateFragment {
            val frag = ListLeaveByDateFragment()
            frag.calendar = calendar
            return frag
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_list_leave_by_date
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = App.userId ?: return
        showLoading()
        val filter = hashMapOf("LoginUserId" to userId,
                "FromDate" to calendar.toStringFormat("yyyy-MM-dd")!!,
                "ToDate" to calendar.toStringFormat("yyyy-MM-dd")!!)
        val weakSelf = WeakReference(this)
        APIClient.shared.api.getAllEvents(filter).applyOn()
                .subscribe({
                    weakSelf.get()?.hideLoading()
                    if (it.data != null) {
                        weakSelf.get()?.showListEvent(it.data!!)
                    } else {
                        weakSelf.get()?.showError(it.message)
                    }
                }, {
                    weakSelf.get()?.showError(it.message)
                    weakSelf.get()?.hideLoading()
                })
    }

    private fun showListEvent(events: List<Event>) {

    }
}