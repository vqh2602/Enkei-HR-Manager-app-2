package com.athsoftware.hrm.views.authen

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.athsoftware.hrm.App
import com.athsoftware.hrm.MainActivity
import com.athsoftware.hrm.R
import com.athsoftware.hrm.base.BaseFragment
import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.helper.extensions.getString
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment(), LoginContractor.View {

    private val presenter = LoginPresenter()

    override fun getPresenter(): IBasePresenter<IBaseView>? {
        return presenter as IBasePresenter<IBaseView>
    }

    override fun getLayoutRes(): Int = R.layout.fragment_login
    override val isTransfStatus = false
    override fun isShowToolbar(): Boolean {
        return false
    }

    private var isSave = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin.setOnClickListener {
            login()
        }

        btnForgot.setOnClickListener {
            showChangePassword()
        }
    }

    private fun login() {
        if (edtPhoneNumber.text.isEmpty()) {
            mActivity.showMessage(R.string.PhoneNotEmpty.getString())
            return
        } else if (edtPassword.text.isEmpty()) {
            mActivity.showMessage(R.string.PassNotEmpty.getString())
            return
        }
        presenter.login(edtPhoneNumber.text.toString(), edtPassword.text.toString(), isSave)
    }

    override fun showHome() {
        activity?.startActivity(MainActivity.newIntent(activity, arrayListOf()))
        activity?.finish()
    }

    override fun showLoginError(error: String) {
        showAlert(error)
    }

    override fun showChangePassword() {
        activity?.startActivity(Intent(activity, ChangePasswordActivity::class.java))
    }
}