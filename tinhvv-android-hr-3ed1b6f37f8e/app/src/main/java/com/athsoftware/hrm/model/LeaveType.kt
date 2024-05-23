package com.athsoftware.hrm.model

import com.google.gson.annotations.SerializedName

data class LeaveType(
        @SerializedName("LeaveOfAbsenceTypeId")
        var leaveOfAbsenceTypeId: Int? = null,
        @SerializedName("TypeName")
        var typeName: String? = null,
        @SerializedName("Description")
        var des: String? = null,
        @SerializedName("MaxDayOff")
        var maxDayOff: String? = null,
        @SerializedName("AvailableTime")
        var availableTime: String? = null,
        @SerializedName("ActiveStatus")
        var activeStatus: String? = null,
        @SerializedName("CreatedByUserId")
        var createdByUserId: String? = null,
        @SerializedName("CreatedOnDate")
        var createdOnDate: String? = null,
        @SerializedName("LastModifiedByUserId")
        var lastModifiedByUserId: String? = null,
        @SerializedName("LastModifiedOnDate")
        var lastModifiedOnDate: String? = null
) {
        override fun toString(): String {
                return typeName ?: ""
        }
}