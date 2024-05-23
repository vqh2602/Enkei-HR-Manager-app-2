package com.athsoftware.hrm.views.leave

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.athsoftware.hrm.App
import com.athsoftware.hrm.R
import com.athsoftware.hrm.base.BaseFragment
import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.common.Constants
import com.athsoftware.hrm.helper.extensions.*
import com.athsoftware.hrm.model.Event
import com.athsoftware.hrm.model.EventStatus
import com.athsoftware.hrm.model.LeaveType
import com.athsoftware.hrm.model.RegisterHoliday
import com.athsoftware.hrm.views.home.HomeFragment
import com.haibin.calendarview.CalendarView
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_create_leave.*
import java.util.*

class CreateLeaveFragment : BaseFragment(), CreateLeaveContractor.View {

    companion object {
        fun getInstance(bundle: Bundle): CreateLeaveFragment {
            val frag = CreateLeaveFragment()
            frag.arguments = bundle
            return frag
        }
    }

    private var isEdit = false
    private var leave: Event? = null

    override fun showListType(types: List<LeaveType>) {
        this.types = types
        showChooseType()
    }

    override fun showSuccess(event: Event?) {
        if (event == null) return
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

    override fun showDayOff(it: Float) {
        tvRemainDayOff.text = it.toOffDay()
        numberRemainDayOff = it
    }

    private val presenter = CreateLeavePresenter()

    override fun getPresenter(): IBasePresenter<IBaseView>? {
        return presenter as IBasePresenter<IBaseView>
    }

    override fun getLayoutRes(): Int = R.layout.fragment_create_leave
    override val isTransfStatus = false
    override fun isShowToolbar(): Boolean {
        return false
    }

    private var isDayOff = true
    private var numberRemainDayOff = 0.0f
    private var startDate = Calendar.getInstance()
    private var endDate = Calendar.getInstance()
    private var type: LeaveType? = null
    private var types: List<LeaveType>? = null
    private var typePartOfDay = 0
    private var partTitle = listOf(R.string.allDay.getString(),
            R.string.morning.getString(),
            R.string.afternoon.getString())
    private var registerHoliday = RegisterHoliday()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isEdit = arguments?.getBoolean("isEdit") ?: false
        leave = arguments?.getSerializable("leave") as? Event
        leave?.let { event ->
            event.startDate?.toCalendar(Constants.DateFormat.dateVi)?.let {
                startDate = it
            }
            event.endDate?.toCalendar(Constants.DateFormat.dateVi)?.let {
                endDate = it
            }
            isDayOff = event.leaveGroup == 0
            if (isDayOff) {
                checkShift.setTextColor(R.color.textBlack.getColor())
                checkDays.setTextColor(R.color.colorPrimary.getColor())
                tvOffTypeTitle.text = R.string.endDay.getString()
            } else {
                checkShift.setTextColor(R.color.colorPrimary.getColor())
                checkDays.setTextColor(R.color.textBlack.getColor())
            }
            typePartOfDay = event.thoiGianNghi?.toInt() ?: 0
            dayOffChecked(isDayOff)
            type = LeaveType(event.leaveTypeId, event.leaveTypeName)
            tvLeaveType.text = event.leaveTypeName
            edtNote.setText(event.des)
        }
        checkDays.setOnClickListener {
            dayOffChecked(true)
        }
        checkShift.setOnClickListener {
            dayOffChecked(false)
        }
        tvEndDate.text = startDate.toStringFormat(Constants.DateFormat.dateVi)
        tvStartDate.text = endDate.toStringFormat(Constants.DateFormat.dateVi)

        listOf<View>(tvEndDate, ic_lock4).forEach {
            it.setOnClickListener { _ ->
                if (!isDayOff) {
                    context?.let { context ->
                        showSelectionPartOfDay(context, null, partTitle) {
                            typePartOfDay = it
                            tvEndDate.text = partTitle[typePartOfDay]
                        }
                    }
                } else {
                    context?.let { context ->
                        showSelectDate(context, minDate = startDate, default = endDate) {
                            endDate = it
                            tvEndDate.text = endDate.toStringFormat()
                            highlightCalendar()
                        }
                    }
                }
            }
        }
        listOf<View>(tvStartDate, ic_lock).forEach {
            it.setOnClickListener { _ ->
                val today = Calendar.getInstance()
                context?.let { context ->
                    showSelectDate(context, minDate = today, default = startDate) {
                        startDate = it
                        startDate.resetTime()
                        calendarView?.scrollToCalendar(startDate.year(), startDate.month() + 1, startDate.day())
                        tvStartDate.text = startDate.toStringFormat()
                        if (endDate < startDate) {
                            endDate = startDate
                            tvEndDate.text = endDate.toStringFormat()
                        }
                        highlightCalendar()
                    }
                }
            }
        }

        tvLeaveType.setOnClickListener {
            showChooseType()
        }
        btnSave.setOnClickListener {
            updateRegisterData()
            if (validate()) {
                presenter.createLeave(registerHoliday, false, "", isEdit, leave?.registerId)
            }
        }
        btnSaveAndSend.setOnClickListener { _ ->
            updateRegisterData()
            if (validate()) {
                presenter.createLeave(registerHoliday, true, "", isEdit, leave?.registerId)
            }
        }
        btnBack.setOnClickListener {
            activity?.finish()
        }
        presenter.getDayOff()
        currentDateLabel.text = Calendar.getInstance().toStringFormat("MM/YYYY")
        showCalendar()
        calendarView.setOnMonthChangeListener { year, month ->
            currentDateLabel.text = "$month/$year"
            val pos = App.shared().loadLeaveDay("$year") {
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


    private fun highlightCalendar() {
        val pos = Single.create<List<Calendar?>> {
            val listDate = mutableListOf<Calendar>()
            startDate?.let { start ->
                val startClone = start.clone() as Calendar
                endDate?.let { end ->
                    listDate.add(startClone.clone() as Calendar)
                    if (end > startClone) {
                        while (startClone <= end) {
                            listDate.add(startClone.clone() as Calendar)
                            startClone.add(Calendar.DATE, 1)
                        }
                    }
                }
            }
            val lCal = listDate.distinctBy { it.time }
            it.onSuccess(lCal)
        }.applyOn()
                .subscribe({ cals ->
                    calendarView.setHighlightEvents(cals.map {
                        it!!.toCalendar()
                    })
                    calendarView.update()
                }, {})

        disposables.add(pos)
    }

    private fun showCalendar() {
        App.shared().loadAllEvents { listEvent ->
            val pos = Single.create<List<Calendar?>> { emitter ->
                val listDate = mutableListOf<Calendar>()
                listEvent.forEach {
                    val status = try {
                        EventStatus.valueOf(it.workflowState ?: "")
                    } catch (_: java.lang.Exception) {
                        EventStatus.Default
                    }
                    when (status) {
                        EventStatus.Denial,
                        EventStatus.DMDenialContent,
                        EventStatus.DenialContent,
                        EventStatus.OperatorDenialContent,
                        EventStatus.SMDenialContent,
                        EventStatus.StaffDenialContent -> {

                        }
                        else -> {
                            if (it.createdByUser?.userId == App.userId) {
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
                }
                val lCal = listDate.distinctBy { it.time }
                emitter.onSuccess(lCal)
            }.applyOn()
                    .subscribe({ cals ->
                        calendarView.setListEvents(cals.map {
                            it!!.toCalendar()
                        })
                        calendarView.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {
                            override fun onCalendarOutOfRange(calendar: com.haibin.calendarview.Calendar?) {
                            }

                            override fun onCalendarSelect(calendar: com.haibin.calendarview.Calendar, isClick: Boolean) {
                                val cal = calendar.toCalendar()
                                if (calendarView.isEvent(calendar)) {
                                    openFragment(HomeFragment.getInstance(true, cal))
                                }
                            }
                        })
                        calendarView.update()
                        highlightCalendar()
                    }, {})
            disposables.add(pos)
        }

        val pos = App.shared().loadLeaveDay("${Calendar.getInstance()[Calendar.YEAR]}") {
            calendarView.setLeaveDate(it)
            calendarView.update()
        }
        pos?.let {
            disposables.add(it)
        }
    }

    private fun dayOffChecked(isChecked: Boolean = true) {
        if (isChecked) {
            checkShift.setTextColor(R.color.textBlack.getColor())
            checkDays.setTextColor(R.color.colorPrimary.getColor())
            tvOffTypeTitle.text = R.string.endDay.getString()
            tvEndDate.text = startDate.toStringFormat(Constants.DateFormat.dateVi)
            ic_lock4.setImageResource(R.drawable.date)
            isDayOff = true
        } else {
            checkShift.setTextColor(R.color.colorPrimary.getColor())
            checkDays.setTextColor(R.color.textBlack.getColor())
            tvOffTypeTitle.text = R.string.ca_kip.getString()
            ic_lock4.setImageResource(R.drawable.dropdown)
            tvEndDate.text = partTitle[typePartOfDay]
            isDayOff = false
        }
    }

    private fun validate(): Boolean {
        hideKeyboard()
        if (App.userId == null) return false
        if (type == null) {
            showAlert(R.string.MustChooseLeaveType.getString())
            return false
        }
        startDate.resetTime()
        endDate.resetTime()
        if (isDayOff && startDate > endDate) {
            showAlert(R.string.StartDateEndDate.getString())
            return false
        }
        startDate.resetTime()
        val today = Calendar.getInstance()
        today.resetTime()
        val typeId = type!!.leaveOfAbsenceTypeId
        if (typeId != 3 && typeId != 5 && startDate.timeInMillis - today.timeInMillis < 60 * 60 * 1000 * 24 * 3) {
            showAlert(R.string.CreatedFormEarly3Day.getString())
            return false
        }
        return true
    }

    private fun updateRegisterData() {
        registerHoliday.note = edtNote.text.toString()
        registerHoliday.userId = App.userId
        registerHoliday.startDate = startDate.toStringFormat() ?: ""
        registerHoliday.endDate = endDate.toStringFormat() ?: ""
        registerHoliday.leaveType = type
        registerHoliday.remainDay = numberRemainDayOff.toInt()
        registerHoliday.typeOff = typePartOfDay
        registerHoliday.leaveGroup = if (isDayOff) 0 else 1
    }

    private fun showChooseType() {
        if (types == null) {
            presenter.getListType()
            return
        }
        if (context != null) {
            showSelection(context!!, null, types!!) {
                type = it
                tvLeaveType.text = type?.typeName
            }
        }
    }
}