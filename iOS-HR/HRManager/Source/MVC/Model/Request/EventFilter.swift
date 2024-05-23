//
//  EventFilter.swift
//  HRManager
//
//  Created by Tinh Vu on 1/13/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation

@objc class EventFilter: NSObject {
    var page = 1
    var pageSize = 100
    var textSearch = ""
    var flowState = [EventStatus]()
    var createdByUserId: String?
    var flowCode: String = "QUANLYNGHIPHEP"
    var fromDate: Date?
    var toDate: Date?

    init(page: Int = 1, size: Int = 50, text: String = "", states: [EventStatus] = [], userId: String? = nil) {
        self.page = page
        self.pageSize = size
        self.textSearch = text
        self.flowState = states
        self.createdByUserId = userId
    }

    func toDict() -> [String: Any] {
      var dict = ["PageNumber": page,
                "PageSize": pageSize,
                "TextSearch": textSearch,
                "WorkflowCodeList": ["QUANLYNGHIPHEP"],
                "LoginUserId": createdByUserId ?? ""
                ] as [String : Any]
      if flowState.count > 0 {
        dict["WorkflowStateList"] = flowState.map{$0.rawValue}
      }

        if let from = fromDate {
            dict["FromDate"] = from.toString(dateFormat: "yyyy-MM-dd")
        }

        if let to = toDate {
            dict["ToDate"] = to.toString(dateFormat: "yyyy-MM-dd")
        }
      return dict
    }
}
