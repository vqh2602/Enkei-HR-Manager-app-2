package com.athsoftware.hrm.views.calendar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.athsoftware.hrm.App
import com.athsoftware.hrm.R
import com.athsoftware.hrm.base.BaseFragment
import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.helper.extensions.*
import com.athsoftware.hrm.model.Event
import com.athsoftware.hrm.model.EventStatus
import com.athsoftware.hrm.model.request.EventFilter
import com.athsoftware.hrm.views.home.HomeContractor
import com.athsoftware.hrm.views.home.HomePresenter
import com.athsoftware.hrm.views.leave.LeaveDetailActivity
import com.daimajia.swipe.SwipeLayout
import com.haibin.calendarview.CalendarView
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.item_calendar.view.*
import kotlinx.android.synthetic.main.item_leave.view.*
import java.lang.ref.WeakReference
import java.util.*

class CalendarFragment : BaseFragment(), HomeContractor.View {

    val UPDATE_FLAG = 234
    private val presenter = HomePresenter()
    private var filterEvents: ArrayList<Event> = arrayListOf()
    private var currentSelected = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reloadCalendar()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = Adapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            onRefresh()
        }
    }

    override fun onRefresh() {
        val dayFilter: HashMap<String, Any> = hashMapOf("LoginUserId" to App.userId!!,
                "CurrentDate" to currentSelected.toStringFormat("yyyy-MM-dd")!!)
        presenter.loadCalendar(dict = dayFilter)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_calendar
    }

    override fun getPresenter(): IBasePresenter<IBaseView>? {
        return presenter as IBasePresenter<IBaseView>
    }

    override fun showListEvent(events: List<Event>) {
        if (this.filterEvents.isNotEmpty()) {
            recyclerView?.adapter?.notifyItemRangeRemoved(1, this.filterEvents.size)
        }
        this.filterEvents.clear()
        this.filterEvents.addAll(events)

        recyclerView?.adapter?.notifyItemRangeInserted(1, events.size)
    }

    override fun stopEventSuccess(eventId: String) {
    }

    fun reloadCalendar() {
        App.shared().loadAllEvents {listEvent ->
            val dispose = Single.create<List<Calendar>> { emitter ->
                val listDate = mutableListOf<Calendar>()
                listEvent.forEach {
                    val status = try {
                        EventStatus.valueOf(it.workflowState ?: "")
                    } catch (_: java.lang.Exception) {
                        EventStatus.Default
                    }
                    when(status) {
                        EventStatus.Denial,
                        EventStatus.DMDenialContent,
                        EventStatus.DenialContent,
                        EventStatus.OperatorDenialContent,
                        EventStatus.SMDenialContent,
                        EventStatus.StaffDenialContent -> {

                        }
                        else -> {
                            it.startDate?.toCalendar()?.let { start ->
                                val startClone = start.clone() as Calendar
                                it.endDate?.toCalendar()?.let { end ->
                                    listDate.add(startClone.clone() as Calendar)
                                    if (end > startClone) {
                                        while (startClone <= end) {
                                            listDate.add(startClone.clone() as Calendar)
                                            startClone.add(Calendar.DATE, 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                val lCal = listDate.distinctBy { it.time }
                emitter.onSuccess(lCal)
            }.applyOn()
                    .subscribe({
                        (recyclerView?.adapter as? Adapter)?.listDate = it
                        recyclerView?.adapter?.notifyItemChanged(0)
                    }, {})
            disposables.add(dispose)
        }

        val pos = App.shared().loadLeaveDay("${Calendar.getInstance()[Calendar.YEAR]}") {
            (recyclerView?.adapter as? Adapter)?.listLeaveDate = it
            recyclerView?.adapter?.notifyItemChanged(0)
        }
        pos?.let {
            disposables.add(it)
        }

    }

    inner class Adapter : RecyclerView.Adapter<Holder>() {

        var listDate = listOf<Calendar>()
        var listLeaveDate = listOf<com.haibin.calendarview.Calendar>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            if (viewType == 0) {
                val view = parent.inflate(R.layout.item_calendar)
                return Holder(view)
            }
            val view = parent.inflate(R.layout.item_leave)
            return Holder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getItemCount(): Int {
            return 1 + filterEvents.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: Holder, position: Int) {
            if (position == 0) {
                holder.itemView.calendarView.setLeaveDate(listLeaveDate)
                holder.itemView.calendarView.update()
                holder.itemView.calendarView.setListEvents(listDate.map {
                    it.toCalendar()
                })
                holder.itemView.calendarView.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {
                    override fun onCalendarOutOfRange(calendar: com.haibin.calendarview.Calendar?) {
                    }

                    override fun onCalendarSelect(calendar: com.haibin.calendarview.Calendar, isClick: Boolean) {
                        val cal = calendar.toCalendar()
                        currentSelected = cal
                        if (listDate.any { cal.isSame(it) }) {
                            val dayFilter: HashMap<String, Any> = hashMapOf("LoginUserId" to App.userId!!,
                                    "CurrentDate" to calendar.toCalendar()!!.toStringFormat("yyyy-MM-dd")!!)
                            presenter.loadCalendar(dict = dayFilter)
                        }
                    }
                })
                holder.itemView.run {
                    currentDateLabel.text = "${calendarView.curMonth}/${calendarView.curYear}"
                    calendarView.setOnMonthChangeListener { year, month ->
                        currentDateLabel.text = "$month/$year"
                        val pos = App.shared().loadLeaveDay("$year") {
                            listLeaveDate = it
                            calendarView.setLeaveDate(it)
                            calendarView.update()
                        }
                        pos?.let {
                            disposables.add(it)
                        }
                    }
                    previousButton.setOnClickListener {
                        calendarView.scrollToPre()
                    }
                    forwardButton.setOnClickListener {
                        calendarView.scrollToNext()
                    }
                }
                return
            }
            val event = filterEvents[position - 1]
            holder.itemView.apply {
                clickableView.setOnClickListener { _ ->
                    activity?.let {
                        val notify = Event()
                        notify.registerId = event.registerId
                        val intent = LeaveDetailActivity.getIntent(it, notify)
                        startActivityForResult(intent, UPDATE_FLAG)
                    }
                }
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
                    event.loadUserImage(position - 1) { img, p ->
                        if (img.isNotEmpty()) {
                            weakView.get()?.post {
                                weakView.get()?.loadBase64(img)
                            }
                        } else {
                            weakView.get()?.setImageForName(event.createdByUser?.fullName ?: "")
                        }
                    }
                }
                tvName.text = event.createName
                tvDateCreate.text = event.lastModifiedOnDate?.dateConvertFormat()
                tvLeaveType.text = R.string.Home_LeaveType.getString() + " " + (event.leaveTypeName
                        ?: "")
                var textDays = if (event.countDay != null) {
                    R.string.Home_NumberDay.getString() + " " +
                            "${event.countDay!!.toOffDay()} ${if (event.countDay!! > 1)
                                R.string.Home_Days.getString()
                            else R.string.Home_Day.getString()}"
                } else {
                    ""
                }
                swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
                if (event.workflowState != null) {
                    tvStatus.visible()
                    bgStatus.visible()
                    val status = try {
                        EventStatus.valueOf(event.workflowState ?: "")
                    } catch (_: Exception) {
                        EventStatus.Default
                    }
                    tvStatus.setTextColor(Color.parseColor(status.color()))
                    tvStatus.text = status.des().getString()
                    bgStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(status.color()))
                    when (status) {
                        EventStatus.Draft,
                        EventStatus.Denial,
                        EventStatus.DMDenialContent,
                        EventStatus.DenialContent,
                        EventStatus.OperatorDenialContent,
                        EventStatus.SMDenialContent,
                        EventStatus.StaffDenialContent -> {
                            swipeLayout.isSwipeEnabled = true
                            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, llWrapper)
                        }
                        else -> {
                            swipeLayout.isSwipeEnabled = false
                            swipeLayout.removeAllSwipeDeniers()
                        }
                    }
                } else {
                    tvStatus.gone()
                    bgStatus.gone()
                    swipeLayout.isSwipeEnabled = false
                    swipeLayout.removeAllSwipeDeniers()
                }
                tvReason.text = event.des
                tvComment.text = event.comment
                val startDate = event.startDate?.dateConvertFormat() ?: ""
                val endDate = event.endDate?.dateConvertFormat() ?: ""
                textDays += " "
                textDays += "($startDate - $endDate)"
                tvTotalDays.text = textDays
                btnDelete.setOnClickListener {
                    showConfirm(this@CalendarFragment.context!!, content = R.string.ConfirmDeleteForm.getString(), rightButtonTitle = R.string.Delete.getString(), leftButtonTitle = R.string.cancel.getString(),
                            rightButtonClickHandler = {
                                presenter.stopEvent(event.registerId ?: return@showConfirm)
                            })
                }
            }
        }

    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view)

}