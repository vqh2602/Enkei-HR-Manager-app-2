package com.athsoftware.hrm.model

import com.athsoftware.hrm.R

enum class EventStatus(val data: String, val isFilter: Boolean = false) {
    Draft("Draft", true),
    Stop("Stop", false),
    Denial("Denial", true),
    Approve("Approve", true),
    Publish("Publish", true),
    StopContent("StopContent"),
    DenialContent("DenialContent"),
    QLC1Execute("QLC1Execute"),
    QLC2Execute("QLC2Execute"),
    HCNSExecute("HCNSExecute"),
    StoreContent("StoreContent"),
    OLExecute("OLExecute"),//: Operator Leader duyệt
    SMExecute("SMExecute"),//: Section Manager duyệt
    DMExecute("DMExecute"),//: Department Manager duyệt
    GDExecute("GDExecute"),//: General Director duyệt
    HRExecute("HRExecute"),//: HR duyệt
    OperatorStopContent("OperatorStopContent"),//: ĐKN bị huỷ
    StaffStopContent("StaffStopContent"),//: ĐKN bị huỷ
    SMStopContent("SMStopContent"),//: ĐKN bị huỷ
    DMStopContent("DMStopContent"),//: ĐKN bị huỷ
    OperatorDenialContent("OperatorDenialContent"),//: ĐKN bị từ chối
    StaffDenialContent("StaffDenialContent"),//: ĐKN bị từ chối
    SMDenialContent("SMDenialContent"),//: ĐKN bị từ chối
    DMDenialContent("DMDenialContent"),//: ĐKN bị từ chối
    Default("");
    private val colorStop = "#fab200"
    private val colorDenial = "#ff151f"
    private val colorApprove = "#2c66ef"
    private val colorPublish = "#4ece3d"
    fun color(): String {
        return when (this) {
            Draft -> "#9ea0a5"
            Stop -> colorStop
            Denial -> colorDenial
            Approve -> colorApprove
            Publish -> colorPublish
            StopContent -> colorStop
            DenialContent -> colorDenial
            QLC1Execute -> colorApprove
            QLC2Execute -> colorApprove
            HCNSExecute -> colorApprove
            StoreContent -> colorPublish
            OLExecute -> colorApprove
            SMExecute -> colorApprove
            DMExecute -> colorApprove
            GDExecute -> colorApprove
            HRExecute -> colorApprove
            OperatorStopContent -> colorStop
            StaffStopContent -> colorStop
            SMStopContent -> colorStop
            DMStopContent -> colorStop
            OperatorDenialContent -> colorDenial
            StaffDenialContent -> colorDenial
            SMDenialContent -> colorDenial
            DMDenialContent -> colorDenial
            else -> "#000000"
        }
    }

    fun des(): Int {
        return when (this) {
            Draft -> R.string.Draft
            Stop -> R.string.Stop
            Denial -> R.string.Denial
            Approve -> R.string.Waiting
            Publish -> R.string.Publish
            StopContent -> R.string.Stop
            DenialContent -> R.string.Denial
            QLC1Execute -> R.string.Waiting
            QLC2Execute -> R.string.Waiting
            HCNSExecute -> R.string.Waiting
            StoreContent -> R.string.Publish
            OLExecute -> R.string.Waiting
            SMExecute -> R.string.Waiting
            DMExecute -> R.string.Waiting
            GDExecute -> R.string.Waiting
            HRExecute -> R.string.Waiting
            OperatorStopContent -> R.string.Stop
            StaffStopContent -> R.string.Stop
            SMStopContent -> R.string.Stop
            DMStopContent -> R.string.Stop
            OperatorDenialContent -> R.string.Denial
            StaffDenialContent -> R.string.Denial
            SMDenialContent -> R.string.Denial
            DMDenialContent -> R.string.Denial
            else -> R.string.empty
        }
    }
}

enum class WorkFlow(val data: String) {
    StartProcessing("StartProcessing"),
    Denial("Denial"),
    Stop("Stop"),
    Approve("Approve"),
    Publish("Publish");

    fun des(): Int {
        return when (this) {
            StartProcessing -> R.string.StartProcessing
            Denial -> R.string.Denial
            Stop -> R.string.Stop
            Approve -> R.string.Approve
            Publish -> R.string.Publish
        }
    }
}
