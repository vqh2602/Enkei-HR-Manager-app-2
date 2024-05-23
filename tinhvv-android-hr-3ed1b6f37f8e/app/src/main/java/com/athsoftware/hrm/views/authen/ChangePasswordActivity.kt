package com.athsoftware.hrm.views.authen

import android.os.Bundle
import com.athsoftware.hrm.App
import com.athsoftware.hrm.R
import com.athsoftware.hrm.base.BaseActivity
import com.athsoftware.hrm.helper.KeyboardHelper
import com.athsoftware.hrm.helper.extensions.*
import com.athsoftware.hrm.network.APIClient
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : BaseActivity() {
    override fun isUsingBaseContent(): Boolean {
        return false
    }

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        btnBack.setOnClickListener {
            finish()
        }
        btnFinish.setOnClickListener {
            validate()
        }
    }

    override fun onDestroy() {
        KeyboardHelper.shared.hideSoftKeyboard(this)
        super.onDestroy()
        disposable?.dispose()
    }

    private fun validate() {
        val phone = edtPhoneNumber.text?.toString() ?: ""
        if (phone.isEmpty()) {
            showMessage(R.string.PhoneNotEmpty.getString())
            return
        }
        val old = edtPassword.text?.toString() ?: ""
        if (old.isEmpty()) {
            showMessage(R.string.OldPassNotEmpty.getString())
            return
        }
        val new = edtNewPassword.text?.toString() ?: ""
        if (new.isEmpty()) {
            showMessage(R.string.NewPassNotEmpty.getString())
            return
        }
        val confirm = edtConfirmPassword.text?.toString() ?: ""
        if (confirm.isEmpty()) {
            showMessage(R.string.ConfirmPassNotEmpty.getString())
            return
        }
        if (new == confirm) {

        } else {
            showMessage(R.string.ConfirmPassNotMatch.getString())
            return
        }

        loading.visible()

        disposable = APIClient.shared.api.changePassword(phone,
                new, old).applyOn()
                .subscribe({
                    loading?.gone()
                    if (it.isSuccess) {
                        showConfirm(content = R.string.ChangePassSuccess.getString(), rightButtonTitle = "OK", handlerRight = { dia ->
                            dia.dismiss()
                            finish()
                        })
                    } else {
                        showChangeError(it.message)
                    }
                }, {
                    loading?.gone()
                    showChangeError(it.message)
                })
    }

    private fun showChangeError(message: String? = null) {
        showConfirm(content = message ?: R.string.HaveError.getString(), rightButtonTitle = "OK", handlerRight = { dia ->
            dia.dismiss()
        })
    }
}