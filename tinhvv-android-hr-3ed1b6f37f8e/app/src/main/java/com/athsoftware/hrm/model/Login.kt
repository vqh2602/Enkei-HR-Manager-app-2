package com.athsoftware.hrm.model

import com.google.gson.annotations.SerializedName

/*
 token <- map["Token"]
        userId <- map["UserId"]
        email <- map["Email"]
        fullName <- map["FullName"]
        phoneNumber <- map["PhoneNumber"]
 */
data class Login(
        @SerializedName("Token")
        var token: String? = null,
        @SerializedName("UserId")
        var userId: String? = null,
        @SerializedName("Email")
        var email: String? = null,
        @SerializedName("FullName")
        var fullName: String? = null,
        @SerializedName("PhoneNumber")
        var phoneNumber: String? = null
) {
}