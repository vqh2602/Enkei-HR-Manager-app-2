package com.athsoftware.hrm.views.authen

import com.athsoftware.hrm.App
import com.athsoftware.hrm.R
import com.athsoftware.hrm.common.Constants
import com.athsoftware.hrm.helper.extensions.PreferencesHelper
import com.athsoftware.hrm.helper.extensions.applyOn
import com.athsoftware.hrm.helper.extensions.getString
import com.athsoftware.hrm.network.APIClient
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

class LoginPresenter : LoginContractor.Presenter {
    private var view: WeakReference<LoginContractor.View?>? = null
    private var disposable: Disposable? = null
    override fun attachView(view: LoginContractor.View) {
        this.view = WeakReference(view)
    }

    override fun detachView() {
        disposable?.dispose()
        this.view?.clear()
        this.view = null
    }

    override fun login(email: String, password: String, isSave: Boolean) {
        this.view?.get()?.showLoading()
        disposable = APIClient.shared.api.login(email, password)
                .applyOn()
                .subscribe({ response ->
                    if (response.isSuccess) {
                        App.shared().auth = response.data
                        PreferencesHelper.shared.putValue(Constants.Key.user, Gson().toJson(App.shared().auth))
                        App.registerDeviceToken()
                        this.view?.get()?.showHome()
                    } else {
                        this.view?.get()?.hideLoading()
                        if (response.status == 2) {
                            this.view?.get()?.showChangePassword()
                            return@subscribe
                        }
                        this.view?.get()?.showLoginError(response.message ?: R.string.HaveError.getString())
                    }
                }, { error ->
                    this.view?.get()?.showLoginError(error.message ?: R.string.HaveError.getString())
                    this.view?.get()?.hideLoading()
                })
    }
}