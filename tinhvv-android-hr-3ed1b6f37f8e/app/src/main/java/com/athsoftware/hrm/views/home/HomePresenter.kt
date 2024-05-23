package com.athsoftware.hrm.views.home

import com.athsoftware.hrm.App
import com.athsoftware.hrm.helper.extensions.applyOn
import com.athsoftware.hrm.model.EventStatus
import com.athsoftware.hrm.model.request.EventFilter
import com.athsoftware.hrm.network.APIClient
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class HomePresenter : HomeContractor.Presenter {
    override fun loadEvents(filter: EventFilter, isShowLoading: Boolean, dict: HashMap<String, Any>?) {
        disposable?.dispose()
        if (isShowLoading)
            view?.get()?.showLoading()
        disposable = APIClient.shared.api.getAllEvents(dict ?: filter.toDict())
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.data != null) {
                        view?.get()?.showListEvent(it.data!!)
                    } else {
                        view?.get()?.showError(it.message)
                    }
                }, {
                    view?.get()?.showError(it.message)
                    view?.get()?.hideLoading()
                })
    }

    override fun loadCalendar(isShowLoading: Boolean, dict: HashMap<String, Any>?) {
        disposable?.dispose()
        if (isShowLoading)
            view?.get()?.showLoading()
        disposable = APIClient.shared.api.getCalendarEvents(dict ?: hashMapOf())
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.data != null) {
                        view?.get()?.showListEvent(it.data!!)
                    } else {
                        view?.get()?.showError(it.message)
                    }
                }, {
                    view?.get()?.showError(it.message)
                    view?.get()?.hideLoading()
                })
    }

    override fun stopEvent(eventId: String) {
        disposable?.dispose()
        view?.get()?.showLoading()
        disposable = APIClient.shared.api.deleteEvent(eventId)
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.data != null) {
                        view?.get()?.stopEventSuccess(eventId)
                    } else {
                        view?.get()?.showError(it.message)
                    }
                }, {
                    view?.get()?.showError(it.message)
                    view?.get()?.hideLoading()
                })
    }

    private var view: WeakReference<HomeContractor.View?>? = null
    private var disposable: Disposable? = null
    override fun attachView(view: HomeContractor.View) {
        this.view = WeakReference(view)
    }

    override fun detachView() {
        disposable?.dispose()
        this.view?.clear()
        this.view = null
    }
}