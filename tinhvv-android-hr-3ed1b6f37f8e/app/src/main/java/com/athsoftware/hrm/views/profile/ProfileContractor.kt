package com.athsoftware.hrm.views.profile

import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.model.User


interface ProfileContractor {
    interface View : IBaseView {
        fun showProfile(user: User)
        fun showDayOff(dayOff: Float)
    }

    interface Presenter : IBasePresenter<View> {
        fun getProfile()
        fun getDayOff()
    }
}