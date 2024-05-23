package com.athsoftware.hrm.model

import com.google.gson.annotations.SerializedName

data class ProcessResult(
        @SerializedName("IsSuccess")
        var isSuccess: String? = null) {
}