package com.athsoftware.hrm.model

import com.google.gson.annotations.SerializedName

data class RegisterResult(
        @SerializedName("RegisterId")
        var registerId: String) {
}