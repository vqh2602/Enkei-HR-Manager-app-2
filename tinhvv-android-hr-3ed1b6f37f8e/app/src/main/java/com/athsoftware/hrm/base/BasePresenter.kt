package com.athsoftware.hrm.base

interface IBasePresenter <V: IBaseView> {
    fun attachView(view: V)
    fun detachView()
}