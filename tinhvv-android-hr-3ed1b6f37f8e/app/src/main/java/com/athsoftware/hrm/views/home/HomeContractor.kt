package com.athsoftware.hrm.views.home

import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.model.Event
import com.athsoftware.hrm.model.request.EventFilter


interface HomeContractor {
    interface View : IBaseView {
        fun showListEvent(events: List<Event>)
        fun stopEventSuccess(eventId: String)
    }

    interface Presenter : IBasePresenter<View> {
        fun loadEvents(filter: EventFilter, isShowLoading: Boolean = true, dict: HashMap<String, Any>? = null)
        fun loadCalendar(isShowLoading: Boolean = true, dict: HashMap<String, Any>? = null)
        fun stopEvent(eventId: String)
    }
}