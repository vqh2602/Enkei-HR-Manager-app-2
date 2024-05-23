package com.athsoftware.hrm.views.notification

import com.athsoftware.hrm.App
import com.athsoftware.hrm.helper.extensions.applyOn
import com.athsoftware.hrm.network.APIClient
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

class NotificationPresenter : NotificationContractor.Presenter {
    override fun getNotifications() {
        val userId = App.shared().auth?.userId ?: return
        view?.get()?.showLoading()
        disposable = APIClient.shared.api.getNotifications(userId)
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.data != null) {
                        view?.get()?.showNotifications(it.data!!)
                    } else {
                        view?.get()?.showError(it.message)
                    }
                }, {
                    view?.get()?.showError(it.message)
                    view?.get()?.hideLoading()
                })
    }

    private var view: WeakReference<NotificationContractor.View?>? = null
    private var disposable: Disposable? = null
    override fun attachView(view: NotificationContractor.View) {
        this.view = WeakReference(view)
    }

    override fun detachView() {
        disposable?.dispose()
        this.view?.clear()
        this.view = null
    }
}