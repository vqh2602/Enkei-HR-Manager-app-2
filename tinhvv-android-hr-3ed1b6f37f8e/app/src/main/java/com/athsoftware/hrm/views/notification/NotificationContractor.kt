package com.athsoftware.hrm.views.notification

import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.model.Notification


interface NotificationContractor {
    interface View : IBaseView {
        fun showNotifications(notifications: List<Notification>)
    }

    interface Presenter : IBasePresenter<View> {
        fun getNotifications()
    }
}