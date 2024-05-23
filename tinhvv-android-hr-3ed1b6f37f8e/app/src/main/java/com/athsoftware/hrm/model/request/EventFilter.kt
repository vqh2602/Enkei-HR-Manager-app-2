package com.athsoftware.hrm.model.request

import com.athsoftware.hrm.model.EventStatus
import com.google.gson.annotations.SerializedName

data class EventFilter(
        @SerializedName("PageNumber")
        var page: Int = 1,
        @SerializedName("PageSize")
        var pageSize: Int = 100,
        @SerializedName("TextSearch")
        var textSearch: String = "",
        @SerializedName("WorkflowCodeList")
        var flowState: ArrayList<EventStatus> = arrayListOf(),
        @SerializedName("LoginUserId")
        var createdByUserId: String? = null,
        var flowCode: String = "QUANLYNGHIPHEP") {
    fun toDict(): Map<String, Any> {
        val map = hashMapOf("PageNumber" to page, "PageSize" to pageSize,
                "TextSearch" to textSearch, "LoginUserId" to (createdByUserId ?: ""))
        if (flowState.isNotEmpty()) {
            map["WorkflowStateList"] = flowState.map { it.data }
        }
        return map
    }

}