package com.athsoftware.hrm.views.authen

import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView


interface LoginContractor {
    interface View : IBaseView {
        fun showHome()
        fun showLoginError(error: String)
        fun showChangePassword()
    }

    interface Presenter : IBasePresenter<View> {
        fun login(email: String, password: String, isSave: Boolean)
    }
}