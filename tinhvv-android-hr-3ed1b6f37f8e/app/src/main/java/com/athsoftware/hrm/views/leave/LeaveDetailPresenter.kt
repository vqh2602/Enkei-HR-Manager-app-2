package com.athsoftware.hrm.views.leave

import com.athsoftware.hrm.App
import com.athsoftware.hrm.helper.extensions.applyOn
import com.athsoftware.hrm.network.APIClient
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

class LeaveDetailPresenter : LeaveDetailContractor.Presenter {
    override fun getNextFlow(id: String) {
        val userId = App.userId ?: return
        view?.get()?.showButtonProgress()
        val dis = APIClient.shared.api.getWorkFlow(id, userId)
                .applyOn()
                .subscribe({
                    view?.get()?.showButtonProgress(false)
                    if (it.data != null) {
                        view?.get()?.showNextFlow(it.data!!)
                    }
                }, {
                    view?.get()?.showButtonProgress(false)
                    it.printStackTrace()
                })
        disposable.add(dis)
    }

    override fun process(id: String, comment: String?, command: String) {
        val userId = App.userId ?: return
        view?.get()?.showLoading()
        val dis = APIClient.shared.api.process(id, id, command, comment, userId, userId)
                .flatMap { APIClient.shared.api.getEventDetail(id) }
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.isSuccess) {
                        if (it.data != null) {
                            view?.get()?.showSuccess(it.data)
                        }
                    } else {
                        view?.get()?.showError(it.message)
                    }
                }, {
                    view?.get()?.hideLoading()
                    view?.get()?.showError(it.message)
                    it.printStackTrace()
                })
        disposable.add(dis)
    }

    override fun getEventDetail(id: String) {
        view?.get()?.showLoading()
        val dis = APIClient.shared.api.getEventDetail(id)
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.data != null) {
                        view?.get()?.showEventDetail(it.data!!)
                        getNextFlow(id)
                    } else {
                        view?.get()?.showNextFlow(listOf())
                    }
                }, {
                    view?.get()?.hideLoading()
                    view?.get()?.showNextFlow(listOf())
                    it.printStackTrace()
                })
        disposable.add(dis)
    }

    override fun deleteEvent(id: String) {
        view?.get()?.showLoading()
        val dis = APIClient.shared.api.deleteEvent(id)
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.isSuccess) {
                        view?.get()?.showDeleteSuccess(id)
                    } else {
                        view?.get()?.showError(it.message)
                    }
                }, {
                    view?.get()?.hideLoading()
                    view?.get()?.showError(it.message)
                    it.printStackTrace()
                })
        disposable.add(dis)
    }

    private var view: WeakReference<LeaveDetailContractor.View?>? = null
    private var disposable: ArrayList<Disposable> = arrayListOf()
    override fun attachView(view: LeaveDetailContractor.View) {
        this.view = WeakReference(view)
    }

    override fun detachView() {
        disposable.forEach {
            it.dispose()
        }
        this.view?.clear()
        this.view = null
    }
}