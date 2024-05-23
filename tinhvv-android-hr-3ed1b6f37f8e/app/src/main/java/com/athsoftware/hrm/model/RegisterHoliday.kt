package com.athsoftware.hrm.model

import com.athsoftware.hrm.helper.extensions.toStringFormat
import java.util.*

data class RegisterHoliday(
        var leaveGroup: Int = 0,
        var leaveType: LeaveType? = null,
        var note: String = "",
        var startDate: String = Date().toStringFormat("dd/MM/yyyy")!!,
        var endDate: String = Date().toStringFormat("dd/MM/yyyy")!!,
        var userId: String? = null,
        var typeOff: Int = 0,
        var remainDay: Int = 0
)