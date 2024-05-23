package com.athsoftware.hrm.model

import com.athsoftware.hrm.helper.extensions.toDate
import com.google.gson.annotations.SerializedName

/*
id <- map["Id"]
    notificationCampaignId <- map["NotificationCampaignId"]
    userId <- map["UserId"]
    readStatus <- map["ReadStatus"]
    lastModifiedOnDate <- map["LastModifiedOnDate"]
    title <- map["Title"]
 */
data class Notification(
        @SerializedName("Id")
        var id: Int? = null,
        @SerializedName("NotificationCampaignId")
        var notificationCampaignId: Int? = null,
        @SerializedName("UserId")
        var userId: String? = null,
        @SerializedName("ReadStatus")
        var readStatus: Boolean? = null,
        @SerializedName("LastModifiedOnDate")
        var lastModifiedOnDate: String? = null,
        @SerializedName("Title")
        var title: String? = null,
        @SerializedName("RegisterId")
        var registerId: String? = null
) {
}