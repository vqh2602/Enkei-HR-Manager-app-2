package com.athsoftware.hrm.views

import com.athsoftware.hrm.views.MainContractor
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

/**
 * Created by tinhvv on 11/13/18.
 */
class MainPresenter : MainContractor.Presenter {
    private var view: WeakReference<MainContractor.View?>? = null
    private var disposable: Disposable? = null
    override fun attachView(view: MainContractor.View) {
        this.view = WeakReference(view)
    }

    override fun detachView() {
        disposable?.dispose()
        this.view?.clear()
        this.view = null
    }
}