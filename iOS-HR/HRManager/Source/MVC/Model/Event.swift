//
//  Event.swift
//  HRManager
//
//  Created by Tinh Vu on 1/13/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import ObjectMapper

class Event : ServiceModel {
    var leaveGroup : Int = 0
    var leaveTypeId : Int = 0
    var leaveTypeName : String?
    var leaveOfAbsenceType : String?
    var des : String?
    var startDate : String?
    var endDate : String?
    var countHour : Int?
    var countDay : Float?
    var registerUserId : String?
    var approveUserId : String?
    var status : String?
    var reseanRefusal : String?
    var comment : String?
    var cCEmail : String?
    var leaveRemaining : Int?
    var thoiGianNghi : Int = 0
    var listWorkflow : String?
    var registerId : String?
    var createdOnDate : String?
    var lastModifiedOnDate : String?
    var publishOnDate : String?
    var transitionTime : String?
    var createdByUser : User?
    var lastModifiedByUser : User?
    var registerUser : String?
    var approveUser : String?
    var publishByUser : String?
    var transitionUser : User?
    var workflowCode : String?
    var workflowState : String?
    var allowedToEmployeeNames : String?
    var workflowPreviousState : String?
    var registerCategoryCode: String?

    var state: WorkFlow {
        guard let stateString = workflowState else {
            return .Draft
        }
        return WorkFlow(rawValue: stateString) ?? .Draft
    }

    var createdBy: String {
        return "\(createdByUser?.fullName ?? "") - \(registerCategoryCode ?? "")"
    }

    var createdDate: Date? {
        guard let dateString = startDate else {
            return nil
        }
        return dateString.toDate()
    }

    var endedDate: Date? {
        guard let endString = endDate else {
            return nil
        }
        return endString.toDate()
    }

    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {

        leaveGroup <- map["LeaveGroup"]
        leaveTypeId <- map["LeaveTypeId"]
        leaveTypeName <- map["LeaveTypeName"]
        leaveOfAbsenceType <- map["LeaveOfAbsenceType"]
        des <- map["Description"]
        startDate <- map["StartDate"]
        endDate <- map["EndDate"]
        countHour <- map["CountHour"]
        countDay <- map["CountDay"]
        registerUserId <- map["RegisterUserId"]
        approveUserId <- map["ApproveUserId"]
        status <- map["Status"]
        reseanRefusal <- map["ReseanRefusal"]
        comment <- map["Comment"]
        cCEmail <- map["CCEmail"]
        leaveRemaining <- map["LeaveRemaining"]
        thoiGianNghi <- map["ThoiGianNghi"]
        listWorkflow <- map["ListWorkflow"]
        registerId <- map["RegisterId"]
        createdOnDate <- map["CreatedOnDate"]
        lastModifiedOnDate <- map["LastModifiedOnDate"]
        publishOnDate <- map["PublishOnDate"]
        transitionTime <- map["TransitionTime"]
        createdByUser <- map["CreatedByUser"]
        lastModifiedByUser <- map["LastModifiedByUser"]
        registerUser <- map["RegisterUser"]
        approveUser <- map["ApproveUser"]
        publishByUser <- map["PublishByUser"]
        transitionUser <- map["TransitionUser"]
        workflowCode <- map["WorkflowCode"]
        workflowState <- map["WorkflowState"]
        allowedToEmployeeNames <- map["AllowedToEmployeeNames"]
        workflowPreviousState <- map["WorkflowPreviousState"]
        registerCategoryCode <- map["RegisterCategoryCode"]
    }

}

class User: ServiceModel {
    var applicationId : String?
    var pwd : String?
    var active : Bool?
    var roles : String?
    var rights : String?
    var newPwd : String?
    var isExpire : Bool?
    var userId : String?
    var userName : String?
    var fullName : String?
    var position : String?
    var mobile : String?
    var staffCode : String?
    var staffImg: String?

    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {

        applicationId <- map["ApplicationId"]
        pwd <- map["Pwd"]
        active <- map["Active"]
        roles <- map["Roles"]
        rights <- map["Rights"]
        newPwd <- map["newPwd"]
        isExpire <- map["isExpire"]
        userId <- map["UserId"]
        userName <- map["UserName"]
        fullName <- map["FullName"]
        position <- map["Position"]
        mobile <- map["Mobile"]
        staffCode <- map["StaffCode"]
        staffImg <- map["StaffImg"]
    }
}
