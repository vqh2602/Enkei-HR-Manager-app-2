package com.athsoftware.hrm.views.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.athsoftware.hrm.App
import com.athsoftware.hrm.MainActivity
import com.athsoftware.hrm.R
import com.athsoftware.hrm.base.BaseActivity
import com.athsoftware.hrm.base.BaseFragment
import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.helper.extensions.*
import com.athsoftware.hrm.model.Event
import com.athsoftware.hrm.model.EventStatus
import com.athsoftware.hrm.model.request.EventFilter
import com.athsoftware.hrm.views.calendar.CalendarFragment
import com.athsoftware.hrm.views.leave.CreateLeaveActivity
import com.athsoftware.hrm.views.leave.LeaveDetailActivity
import com.daimajia.swipe.SwipeLayout
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_filter_status.view.*
import kotlinx.android.synthetic.main.item_leave.view.*
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*


class HomeFragment : BaseFragment(), HomeContractor.View {

    val CREATE_FLAG = 232
    val UPDATE_FLAG = 234
    var isShowByDate = false
    var dayLoad: Calendar? = null
    var dayFilter: HashMap<String, Any>? = null

    companion object {
        fun getInstance(isShowByDate: Boolean = false, dayLoad: Calendar): HomeFragment {
            val frag = HomeFragment()
            frag.isShowByDate = isShowByDate
            frag.dayLoad = dayLoad
            return frag
        }
    }

    override fun showListEvent(events: List<Event>) {
        if (!isShowByDate && events.size >= eventFilter.pageSize) {
            recyclerView?.addOnScrollListener(scrollListener)
        }
        if (eventFilter.page <= 1) {
            this.filterEvents.forEach {
                it.disposable?.dispose()
            }
            this.filterEvents.clear()
        }
        this.filterEvents.addAll(events)
        if (eventFilter.textSearch.isEmpty() || eventFilter.flowState.isEmpty() && !isShowByDate) {
            ((activity as? MainActivity)?.tab3Fragment as? CalendarFragment)?.reloadCalendar()
        }
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    override fun stopEventSuccess(eventId: String) {
        filterEvents = filterEvents.filter { it.registerId != eventId } as ArrayList<Event>
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    private val presenter = HomePresenter()
    private var filterEvents: ArrayList<Event> = arrayListOf()
    private val eventFilter = EventFilter()

    override fun getPresenter(): IBasePresenter<IBaseView>? {
        return presenter as IBasePresenter<IBaseView>
    }

    override fun getLayoutRes(): Int = R.layout.fragment_home
    override val isTransfStatus = false
    override fun isShowToolbar(): Boolean {
        return false
    }

    private lateinit var layoutManager: LinearLayoutManager

    var scrollListener: RecyclerView.OnScrollListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.shared().loadAllEvents()
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = Adapter()
        eventFilter.createdByUserId = App.userId
        setupFilter()
        if (isShowByDate && dayLoad != null) {
            dayFilter = hashMapOf("LoginUserId" to App.userId!!,
                    "CurrentDate" to dayLoad!!.toStringFormat("yyyy-MM-dd")!!)
            actionBar.visible()
            tvTitle.text = dayLoad?.toStringFormat("yyyy-MM-dd")
            edtSearch.gone()
            filterView.gone()
            btnAdd.gone()
            btnBack.setOnClickListener {
                (activity as? BaseActivity)?.onBackPressed()
            }
        }
        btnAdd.setOnClickListener {
            startActivityForResult(Intent(activity, CreateLeaveActivity::class.java), CREATE_FLAG)
        }
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = edtSearch.text.toString()
                eventFilter.textSearch = query
                eventFilter.page = 0
                presenter.loadEvents(eventFilter, true)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        (activity as? MainActivity)?.setOnRightClickListener {
            filterView.visible(!filterView.isVisble())
        }
        refreshView.setOnRefreshListener {
            refreshView.isRefreshing = false
            eventFilter.page = 1
            presenter.loadEvents(eventFilter, dict = dayFilter)
        }
        presenter.loadEvents(eventFilter, dict = dayFilter)
        initLoadmore()
    }

    private fun setupFilter() {
        listFilter.removeAllViews()
        EventStatus.values().forEach {
            if (!it.isFilter) {
                return@forEach
            }
            val view = LayoutInflater.from(context).inflate(R.layout.item_filter_status, listFilter, false)
            view.tag = it
            view.tvFilterStatus.text = it.des().getString()
            view.tvFilterStatus.setTextColor(Color.parseColor(it.color()))
            view.tvFilterStatus.isSelected = false
            view.setOnClickListener(onFilterStatusClick)
            listFilter.addView(view)
        }
    }

    private val onFilterStatusClick = View.OnClickListener { view ->
        val status = view.tag as EventStatus
        if (!view.isSelected) {
            view.isSelected = true
            view.bgFilterStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(status.color()))
            view.bgFilterStatus.alpha = 0.2f
            if (!eventFilter.flowState.any { it.color() == status.color() }) {
                eventFilter.flowState.add(status)
                onRefresh()
            }
        } else {
            view.isSelected = false
            view.bgFilterStatus.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            view.bgFilterStatus.alpha = 1f
            if (eventFilter.flowState.any { it.color() == status.color() }) {
                eventFilter.flowState.remove(status)
                onRefresh()
            }
        }
    }

    override fun onRefresh() {
        App.shared().loadAllEvents()
        eventFilter.page = 1
        presenter.loadEvents(eventFilter)
    }

    override fun onLoadmore() {
        if (isShowByDate) return
        eventFilter.page = eventFilter.page + 1
        presenter.loadEvents(eventFilter, false)
    }

    private fun initLoadmore() {
        scrollListener = object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = recyclerView.layoutManager.itemCount
                val visibleItemCount = recyclerView.layoutManager.childCount
                val firstVisible = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                //Check number hidden item in adapter is
                if (dy > 0 && (visibleItemCount + firstVisible) >= totalItemCount) {
                    //Remove listener load more if is start loading more
                    recyclerView.removeOnScrollListener(scrollListener)
                    this@HomeFragment.onLoadmore()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            onRefresh()
        }
    }

    inner class Adapter : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = parent.inflate(R.layout.item_leave)
            return Holder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getItemCount(): Int {
            return filterEvents.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: Holder, position: Int) {
            val event = filterEvents[position]
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
                    event.loadUserImage(position) { img, p ->
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
                    showConfirm(this@HomeFragment.context!!, content = R.string.ConfirmDeleteForm.getString(), rightButtonTitle = R.string.Delete.getString(), leftButtonTitle = R.string.cancel.getString(),
                            rightButtonClickHandler = {
                                presenter.stopEvent(event.registerId ?: return@showConfirm)
                            })
                }
            }
        }

    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view)

}