package com.athsoftware.hrm.views.profile

import com.athsoftware.hrm.App
import com.athsoftware.hrm.helper.extensions.applyOn
import com.athsoftware.hrm.network.APIClient
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

class ProfilePresenter : ProfileContractor.Presenter {
    override fun getProfile() {
        val userId = App.shared().auth?.userId ?: return
        view?.get()?.showLoading()
        val dis = APIClient.shared.api.getProfile(userId)
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.isSuccess && it.data != null) {
                        view?.get()?.showProfile(it.data!!)
                    } else {
                        view?.get()?.showError(it.message)
                    }
                }, {
                    view?.get()?.showError(it.message)
                    view?.get()?.hideLoading()
                })
        disposable.add(dis)
    }

    override fun getDayOff() {
        val userId = App.shared().auth?.userId ?: return
        val dis = APIClient.shared.api.getOfftime(userId)
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.data != null) {
                        view?.get()?.showDayOff(it.data!!)
                    }
                }, {
                    view?.get()?.hideLoading()
                })
        disposable.add(dis)
    }

    private var view: WeakReference<ProfileContractor.View?>? = null
    private var disposable: ArrayList<Disposable> = arrayListOf()
    override fun attachView(view: ProfileContractor.View) {
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