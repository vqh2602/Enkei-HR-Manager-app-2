//
//  LeaveType.swift
//  HRManager
//
//  Created by Tinh Vu on 1/13/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import ObjectMapper

class LeaveType: ServiceModel {
    var leaveOfAbsenceTypeId : String?
    var typeName : String?
    var des : String?
    var maxDayOff : String?
    var availableTime : String?
    var activeStatus : String?
    var createdByUserId : String?
    var createdOnDate : String?
    var lastModifiedByUserId : String?
    var lastModifiedOnDate : String?

    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        leaveOfAbsenceTypeId <- map["LeaveOfAbsenceTypeId"]
        typeName <- map["TypeName"]
        des <- map["Description"]
        maxDayOff <- map["MaxDayOff"]
        availableTime <- map["AvailableTime"]
        activeStatus <- map["ActiveStatus"]
        createdByUserId <- map["CreatedByUserId"]
        createdOnDate <- map["CreatedOnDate"]
        lastModifiedByUserId <- map["LastModifiedByUserId"]
        lastModifiedOnDate <- map["LastModifiedOnDate"]
    }

    init(id: String, name: String?) {
        super.init()
        self.typeName = name
        self.leaveOfAbsenceTypeId = id
    }

}

