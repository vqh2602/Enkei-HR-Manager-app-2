package com.athsoftware.hrm.base

interface IBaseView {
    fun showLoading(text: String? = null)
    fun hideLoading()

    fun showError(message: String?, isToast: Boolean = true)

    fun showAlert(message: String?) {
        showError(message, false)
    }

    fun onRefresh()
    fun onLoadmore()

    fun back()
}