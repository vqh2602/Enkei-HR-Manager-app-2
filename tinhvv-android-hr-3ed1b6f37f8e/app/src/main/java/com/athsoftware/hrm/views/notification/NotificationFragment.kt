package com.athsoftware.hrm.views.notification

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.athsoftware.hrm.R
import com.athsoftware.hrm.base.BaseFragment
import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.helper.extensions.dateConvertFormat
import com.athsoftware.hrm.helper.extensions.inflate
import com.athsoftware.hrm.helper.extensions.minutesToAgo
import com.athsoftware.hrm.helper.extensions.toDate
import com.athsoftware.hrm.model.Event
import com.athsoftware.hrm.model.Notification
import com.athsoftware.hrm.views.leave.LeaveDetailActivity
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationFragment : BaseFragment(), NotificationContractor.View {
    override fun showNotifications(notifications: List<Notification>) {
        this.notifications = notifications
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    private val presenter = NotificationPresenter()

    override fun getPresenter(): IBasePresenter<IBaseView>? {
        return presenter as IBasePresenter<IBaseView>
    }

    private var notifications: List<Notification> = listOf()

    override fun getLayoutRes(): Int = R.layout.fragment_notification
    override val isTransfStatus = false
    override fun isShowToolbar(): Boolean {
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = Adapter()
        refreshView.setOnRefreshListener {
            refreshView.isRefreshing = false
            presenter.getNotifications()
        }
        presenter.getNotifications()
    }

    override fun onRefresh() {
        presenter.getNotifications()
    }

    inner class Adapter : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = parent.inflate(R.layout.item_notification)
            return Holder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getItemCount(): Int {
            return notifications.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.itemView.apply {
                val notifi = notifications[position]
                clickableView.setOnClickListener { _ ->
                    if (!TextUtils.isEmpty(notifi.registerId)) {
                        val notify = Event()
                        notify.registerId = notifi.registerId
                        activity?.let {
                            val intentDetail = LeaveDetailActivity.getIntent(it, notify)
                            it.startActivity(intentDetail)
                        }
                    }
                }
                tvTime.text = notifi.lastModifiedOnDate?.toDate()?.time?.minutesToAgo()
                tvDescription.text = notifications[position].title
            }
        }
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view)

}