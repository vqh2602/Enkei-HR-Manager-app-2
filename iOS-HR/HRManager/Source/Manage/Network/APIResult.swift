//
//  Response.swift
//  HRManager
//
//  Created by Tinh Vu on 1/13/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import ObjectMapper

class APIResult<T: BaseMappable>: NSObject {
    var message: String?
    var data: T?
    var dataList: [T]?
    var successed: Bool {
        return status == 1
    }
    var status: Int = 0
    init(_ dict: [String: Any]) {
        if let status = dict["Status"] as? Int {
            self.status = status
        }

        if let message = dict["Message"] as? String {
            self.message = message
        }

        if let dataDict = dict["Data"] as? [String: Any] {
            data = T(JSON: dataDict)
        } else if let dictList = dict["Data"] as? [[String: Any]] {
            dataList = dictList.map{ T(JSON: $0) }.compactMap{ $0 }
        } else if let _ = dict["Data"] as? [Any] {
            dataList = []
        }
    }
}
