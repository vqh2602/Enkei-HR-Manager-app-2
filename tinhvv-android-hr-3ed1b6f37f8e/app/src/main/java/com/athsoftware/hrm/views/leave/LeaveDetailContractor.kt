package com.athsoftware.hrm.views.leave

import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.model.Event


interface LeaveDetailContractor {
    interface View : IBaseView {
        fun showNextFlow(flows: List<String>)
        fun showEventDetail(event: Event)
        fun showSuccess(event: Event?)
        fun showDeleteSuccess(eventId: String)
        fun showButtonProgress(isShow: Boolean = true)
    }

    interface Presenter : IBasePresenter<View> {
        fun getNextFlow(id: String)
        fun process(id: String, comment: String?, command: String)
        fun getEventDetail(id: String)
        fun deleteEvent(id: String)
    }
}