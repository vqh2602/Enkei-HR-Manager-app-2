//
//  RegisterHolliday.swift
//  HRManager
//
//  Created by Tienvv on 14/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation

class RegisterHolliday: NSObject {
    var leaveGroup: ContentRegisterHollidayCellStatus = .dayOff
    var leaveType: LeaveType?
    var comment: String = ""
    var startDate = Date()
    var endDate = Date()
    var userId: String?
    var typeOff: ShiftBreaks = .allDay
    var remainDay: Float = 0
    
    override init() {
        super.init()
    }

    func toDict() -> [String: Any] {
        if self.leaveGroup == .timeOff {
            self.endDate = self.startDate
        }
        return [
            "LeaveGroup": self.leaveGroup.rawValue,
            "LeaveTypeId": self.leaveType?.leaveOfAbsenceTypeId ?? 0,
            "Description": self.comment,
            "StrStartDate": self.startDate.toString(dateFormat: "dd/MM/yyyy"),
            "StrEndDate": self.endDate.toString(dateFormat: "dd/MM/yyyy"),
            "RegisterUserId": Global.shared.user?.userId ?? "",
            "ThoiGianNghi": self.typeOff.rawValue] as [String : Any]
    }
    
}

class EditHoliday: NSObject {
    var register: RegisterHolliday
    var registerId: String

    init(register: RegisterHolliday, id: String) {
        self.register = register
        self.registerId = id
    }

    func toDict() -> [String: Any] {
        var dict = register.toDict()
        dict.removeValue(forKey: "RegisterUserId")
        dict["RegisterId"] = registerId
        dict["WorkflowCode"] = "QUANLYNGHIPHEP"
        dict["LastModifiedByUserId"] = Global.shared.user?.userId ?? ""
        return dict
    }
}
