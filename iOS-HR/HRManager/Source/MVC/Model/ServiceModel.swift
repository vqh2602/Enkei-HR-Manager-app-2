//
//  ServiceModel.swift
//  HRManager
//
//  Created by Tinh Vu on 1/13/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import ObjectMapper


class ServiceModel: NSObject, Mappable {

    override init() {
        super.init()
    }

    required init?(map: Map) {

    }

    func mapping(map: Map) {

    }

    public func toJSON() -> [String: Any] {
        return Mapper().toJSON(self)
    }
}
