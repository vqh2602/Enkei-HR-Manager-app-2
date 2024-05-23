package com.athsoftware.hrm.views.leave

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.athsoftware.hrm.App
import com.athsoftware.hrm.R
import com.athsoftware.hrm.base.BaseFragment
import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.helper.extensions.*
import com.athsoftware.hrm.model.Event
import com.athsoftware.hrm.model.EventStatus
import com.athsoftware.hrm.model.WorkFlow
import com.athsoftware.hrm.views.leave.LeaveDetailActivity.Companion.KEY_EVENT
import kotlinx.android.synthetic.main.fragment_leave_detail.*
import java.lang.ref.WeakReference

class LeaveDetailFragment : BaseFragment(), LeaveDetailContractor.View {
    override fun showDeleteSuccess(eventId: String) {
        if (successFragment == null) {
            successFragment = LeaveProcessSuccessFragment.getInstance {
                val intent = Intent()
                intent.putExtra(LeaveDetailActivity.KEY_IS_DELETE, true)
                intent.putExtra(LeaveDetailActivity.KEY_EVENT_ID, eventId)
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            }
        }
        openChildFragment(successFragment!!, true, "LeaveProcessSuccessFragment")
    }

    override fun showEventDetail(event: Event) {
        this.event.disposable?.dispose()
        this.event = event
        show()
    }

    override fun showSuccess(event: Event?) {
        if (successFragment == null) {
            successFragment = LeaveProcessSuccessFragment.getInstance {
                val intent = Intent()
                intent.putExtra(LeaveDetailActivity.KEY_EVENT, event)
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            }
        }
        openChildFragment(successFragment!!, true, "LeaveProcessSuccessFragment")
    }

    private var successFragment: LeaveProcessSuccessFragment? = null

    override fun showNextFlow(flows: List<String>) {
        btnSaveAndSend.visible(flows.size > 1)
        btnSave.visible(flows.isNotEmpty())
        btnSave.setOnClickListener(null)
        btnSaveAndSend.setOnClickListener(null)
        flows.getOrNull(0)?.let {
            btnSave.text = WorkFlow.valueOf(it).des().getString()
            btnSave.setOnClickListener { _ ->
                doProcess(it)
            }
        }
        flows.getOrNull(1)?.let {
            btnSaveAndSend.text = WorkFlow.valueOf(it).des().getString()
            btnSaveAndSend.setOnClickListener { _ ->
                doProcess(it)
            }
        }
        val status = try {
            EventStatus.valueOf(event.workflowState ?: "")
        } catch (_: Exception) {
            EventStatus.Default
        }
        if (flows.size == 1 && status == EventStatus.Draft) {
            btnSaveAndSend.visible()
            btnSaveAndSend.text = R.string.edit.getString()
            btnSaveAndSend.setOnClickListener {
                activity?.let { act ->
                    val intent = Intent(act, CreateLeaveActivity::class.java)
                    val bundle = Bundle()
                    bundle.putBoolean("isEdit", true)
                bundle.putSerializable("leave", event)
                intent.putExtras(bundle)
                startActivityForResult(intent, 123)
            }
        }
    }
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 123) {
        if (resultCode == Activity.RESULT_OK) {
            val intent = Intent()
            intent.putExtra(LeaveDetailActivity.KEY_EVENT, data?.getSerializableExtra(LeaveDetailActivity.KEY_EVENT))
            activity?.setResult(Activity.RESULT_OK, intent)
        }
        activity?.finish()
    }
}

private fun doProcess(command: String) {
    if (command == WorkFlow.Stop.data) {
        presenter.deleteEvent(event.registerId ?: return)
        return
    }
    if (command == WorkFlow.StartProcessing.data) {
        presenter.process(event.registerId ?: return, null, command)
        return
    }
    context?.let { context ->
        showEnterComment(context) {
                presenter.process(event.registerId ?: return@showEnterComment, it, command)
            }
        }
    }

    override fun showButtonProgress(isShow: Boolean) {
        buttonProgressBar?.visible(isShow)
    }

    companion object {
        fun getInstance(event: Event): LeaveDetailFragment {
            val frag = LeaveDetailFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_EVENT, event)
            frag.arguments = bundle
            return frag
        }
    }

    private val presenter = LeaveDetailPresenter()
    private lateinit var event: Event

    override fun getPresenter(): IBasePresenter<IBaseView>? {
        return presenter as IBasePresenter<IBaseView>
    }

    override fun getLayoutRes(): Int = R.layout.fragment_leave_detail
    override val isTransfStatus = false
    override fun isShowToolbar(): Boolean {
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        event = arguments?.getSerializable(KEY_EVENT) as? Event ?: return
        show()
        event.registerId?.let {
            presenter.getEventDetail(it)
        }
        btnBack.setOnClickListener {
            activity?.finish()
        }
    }

    private fun show() {
        showAvatar()
        tvName.text = event.createName
        tvDateCreate.text = event.lastModifiedOnDate?.dateConvertFormat()
        tvLeaveType.text = event.leaveTypeName ?: ""
        if (event.createdByUser?.userId != App.userId) {
            llUserProfile.visible()
            tvPosition.text = event.createdByUser?.position
            tvPhone.text = event.createdByUser?.mobile
            tvStaffCode.text = event.createdByUser?.staffCode
        } else {
            llUserProfile.gone()
        }
        if (event.workflowState != null) {
            tvStatus.visible()
            bgStatus.visible()
            val status = try {
                EventStatus.valueOf(event.workflowState!!)
            } catch (_: Exception) {
                EventStatus.Default
            }
            tvStatus.setTextColor(Color.parseColor(status.color()))
            tvStatus.text = status.des().getString()
            bgStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(status.color()))
        } else {
            tvStatus.gone()
            bgStatus.gone()
        }
        tvNote.text = event.des
        tvComment.text = event.comment
        val startDate = event.startDate?.dateConvertFormat() ?: ""
        val endDate = event.endDate?.dateConvertFormat() ?: ""
        tvOffDays.text = "$startDate - $endDate"
        tvOffDaysCount.text = event.countDay?.toOffDay() ?: ""
        if (event.transitionUser?.userId != event.createdByUser?.userId) {
            tvAccepter.text = event.transitionUser?.fullName ?: ""
        } else {
            tvAccepter.text = ""
        }
    }

    private fun showAvatar() {
        val userImage = App.shared().imageCache[event.createdByUser?.userId]
        if (userImage != null) {
            if (userImage.isNotEmpty()) {
                ivAvatar.loadBase64(userImage)
            } else {
                ivAvatar.setImageForName(event.createdByUser?.fullName ?: "")
            }
        } else {
            ivAvatar.setImageForName(event.createdByUser?.fullName ?: "")
            val weakView = WeakReference(ivAvatar)
            event.loadUserImage(0) { img, _ ->
                if (img.isNotEmpty()) {
                    weakView?.get()?.post {
                        weakView.get()?.loadBase64(img)
                    }
                } else {
                    weakView.get()?.setImageForName(event.createdByUser?.fullName ?: "")
                }
            }
        }
    }

}