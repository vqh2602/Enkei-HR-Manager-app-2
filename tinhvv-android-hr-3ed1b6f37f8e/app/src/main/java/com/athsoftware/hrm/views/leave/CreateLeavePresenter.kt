package com.athsoftware.hrm.views.leave

import com.athsoftware.hrm.App
import com.athsoftware.hrm.helper.extensions.applyOn
import com.athsoftware.hrm.model.RegisterHoliday
import com.athsoftware.hrm.model.WorkFlow
import com.athsoftware.hrm.network.APIClient
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

class CreateLeavePresenter : CreateLeaveContractor.Presenter {
    override fun getDayOff() {
        val userId = App.shared().auth?.userId ?: return
        view?.get()?.showLoading()
        disposable = APIClient.shared.api.getOfftime(userId)
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.data != null) {
                        view?.get()?.showDayOff(it.data!!)
                    }
                }, {
                    view?.get()?.hideLoading()
                })
    }

    override fun createLeave(leave: RegisterHoliday, toNext: Boolean, comment: String, isEdit: Boolean, registerId: String?) {
        view?.get()?.showLoading()
        disposable = (if (!isEdit) APIClient.shared.api.registerEvent(leave.typeOff, leave.leaveGroup,
                leave.leaveType?.leaveOfAbsenceTypeId ?: 0,
                leave.note,
                leave.startDate,
                if (leave.typeOff == 0) leave.endDate else leave.startDate,
                leave.userId!!) else APIClient.shared.api.editEvent(registerId,
                leave.typeOff, leave.leaveGroup,
                leave.leaveType?.leaveOfAbsenceTypeId ?: 0,
                leave.note,
                leave.startDate,
                if (leave.typeOff == 0) leave.endDate else leave.startDate,
                leave.userId!!))
                .flatMap {
                    if (it.data?.registerId != null) Single.just(it.data?.registerId!!)
                    else Single.error<String>(Throwable(it.message ?: "id của đăng ký bị trống"))
                }.flatMap { APIClient.shared.api.getEventDetail(it) }
                .applyOn()
                .subscribe({
                    if (it.isSuccess && it.data != null) {
                        if (toNext) {
                            process(it.data?.registerId, comment)
                        }
                        view?.get()?.hideLoading()
                        view?.get()?.showSuccess(it.data)
                    } else {
                        view?.get()?.hideLoading()
                        view?.get()?.showAlert(it.message)
                    }
                }, {
                    view?.get()?.hideLoading()
                    view?.get()?.showAlert(it.message)
                })
    }

    override fun process(id: String?, comment: String) {
        val userId = App.userId ?: return
        disposable = APIClient.shared.api.process(id, id, WorkFlow.StartProcessing.data, comment, userId, userId)
                .applyOn()
                .subscribe({
                    if (it.isSuccess) {
                        view?.get()?.showSuccess(null)
                    } else {
                        view?.get()?.showAlert(it.message)
                    }
                }, {
                    view?.get()?.showAlert(it.message)
                    it.printStackTrace()
                })
    }

    override fun getListType() {
        view?.get()?.showLoading()
        disposable = APIClient.shared.api.getLeaveType()
                .applyOn()
                .subscribe({
                    view?.get()?.hideLoading()
                    if (it.data?.isNotEmpty() == true) {
                        view?.get()?.showListType(it.data!!)
                    } else {
                        view?.get()?.showAlert(it.message)
                    }
                }, {
                    view?.get()?.hideLoading()
                    view?.get()?.showAlert(it.message)
                })
    }

    private var view: WeakReference<CreateLeaveContractor.View?>? = null
    private var disposable: Disposable? = null
    override fun attachView(view: CreateLeaveContractor.View) {
        this.view = WeakReference(view)
    }

    override fun detachView() {
        disposable?.dispose()
        this.view?.clear()
        this.view = null
    }
}