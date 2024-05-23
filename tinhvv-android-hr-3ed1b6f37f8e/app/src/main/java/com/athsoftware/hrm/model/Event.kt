package com.athsoftware.hrm.model

import com.athsoftware.hrm.App
import com.athsoftware.hrm.helper.extensions.applyOn
import com.athsoftware.hrm.network.APIClient
import com.google.gson.annotations.SerializedName
import io.reactivex.disposables.Disposable
import java.io.Serializable
import java.lang.ref.WeakReference

data class Event(
        @SerializedName("LeaveGroup")
        var leaveGroup: Int? = null,
        @SerializedName("LeaveTypeId")
        var leaveTypeId: Int? = null,
        @SerializedName("LeaveTypeName")
        var leaveTypeName: String? = null,
        @SerializedName("LeaveOfAbsenceType")
        var leaveOfAbsenceType: String? = null,
        @SerializedName("Description")
        var des: String? = null,
        @SerializedName("StartDate")
        var startDate: String? = null,
        @SerializedName("EndDate")
        var endDate: String? = null,
        @SerializedName("CountHour")
        var countHour: Float? = null,
        @SerializedName("CountDay")
        var countDay: Float? = null,
        @SerializedName("RegisterUserId")
        var registerUserId: String? = null,
        @SerializedName("ApproveUserId")
        var approveUserId: String? = null,
        @SerializedName("Status")
        var status: String? = null,
        @SerializedName("ReseanRefusal")
        var reseanRefusal: String? = null,
        @SerializedName("Comment")
        var comment: String? = null,
        @SerializedName("CCEmail")
        var cCEmail: String? = null,
        @SerializedName("LeaveRemaining")
        var leaveRemaining: Float? = null,
        @SerializedName("ThoiGianNghi")
        var thoiGianNghi: Float? = null,
        @SerializedName("ListWorkflow")
        var listWorkflow: String? = null,
        @SerializedName("RegisterId")
        var registerId: String? = null,
        @SerializedName("CreatedOnDate")
        var createdOnDate: String? = null,
        @SerializedName("LastModifiedOnDate")
        var lastModifiedOnDate: String? = null,
        @SerializedName("PublishOnDate")
        var publishOnDate: String? = null,
        @SerializedName("TransitionTime")
        var transitionTime: String? = null,
        @SerializedName("CreatedByUser")
        var createdByUser: User? = null,
        @SerializedName("LastModifiedByUser")
        var lastModifiedByUser: User? = null,
        @SerializedName("RegisterUser")
        var registerUser: User? = null,
        @SerializedName("ApproveUser")
        var approveUser: User? = null,
        @SerializedName("TransitionUser")
        var transitionUser: User? = null,
        @SerializedName("WorkflowCode")
        var workflowCode: String? = null,
        @SerializedName("WorkflowState")
        var workflowState: String? = null,
        @SerializedName("AllowedToEmployeeNames")
        var allowedToEmployeeNames: String? = null,
        @SerializedName("WorkflowPreviousState")
        var workflowPreviousState: String? = null,
        @SerializedName("RegisterCategoryCode")
        var registerCategoryCode: String? = null
) : Serializable {
    var disposable: Disposable? = null
    fun loadUserImage(position: Int, listener: (String, Int) -> Unit) {
        val weakListener = WeakReference(listener)
        val userId = createdByUser?.userId ?: kotlin.run {
            weakListener.get()?.invoke("", position)
            return
        }
        val image = App.shared().imageCache[userId]
        if (image != null) {
            weakListener.get()?.invoke(image, position)
            return
        }
        if (App.shared().imageCacheListener[userId] == null) {
            App.shared().imageCacheListener[userId] = mutableListOf(weakListener)
            disposable = APIClient.shared.api.getProfile(userId)
                    .applyOn()
                    .subscribe({
                        val userImage = it.data?.staffImg ?: ""
                        App.shared().imageCache[userId] = userImage
                        App.shared().imageCacheListener[userId]?.forEach {
                            it.get()?.invoke(userImage, position)
                        }
                    }, {
                        App.shared().imageCacheListener[userId]?.forEach {
                            it.get()?.invoke("", position)
                        }
                    })
        } else {
            App.shared().imageCacheListener[userId]!!.add(weakListener)
        }
    }

    var createName: String = ""
        get() = String.format("%s - %s", createdByUser?.fullName ?: "", registerCategoryCode ?: "")
}