package com.athsoftware.hrm.views

import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
/**
 * Created by tinhvv on 11/13/18.
 */
interface MainContractor {
    interface View : IBaseView {

    }

    interface Presenter : IBasePresenter<View> {

    }
}