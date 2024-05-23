package com.athsoftware.hrm.model

import com.google.gson.annotations.SerializedName

class Response<T> {
    @SerializedName("Code")
    var code: Int = 1
    @SerializedName("Status")
    var status: Int = 0
    @SerializedName("Message")
    var message: String? = null
    @SerializedName("Data")
    var data: T? = null

    var isSuccess: Boolean = false
        get() = code == 200 || status == 1

    init {

    }

    constructor()

    constructor(data: T?) {
        this.data = data
    }
}