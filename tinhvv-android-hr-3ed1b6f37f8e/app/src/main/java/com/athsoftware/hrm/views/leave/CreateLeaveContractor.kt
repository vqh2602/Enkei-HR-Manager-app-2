package com.athsoftware.hrm.views.leave

import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.model.Event
import com.athsoftware.hrm.model.LeaveType
import com.athsoftware.hrm.model.RegisterHoliday


interface CreateLeaveContractor {
    interface View : IBaseView {
        fun showSuccess(event: Event?)
        fun showDayOff(it: Float)
        fun showListType(types: List<LeaveType>)
    }

    interface Presenter : IBasePresenter<View> {
        fun getDayOff()
        fun createLeave(leave: RegisterHoliday, toNext: Boolean = true, comment: String, isEdit: Boolean = false, registerId: String? = null)
        fun process(id: String?, comment: String)
        fun getListType()
    }
}