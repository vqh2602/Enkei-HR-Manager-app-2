//
//  RegisterResult.swift
//  HRManager
//
//  Created by Tinh Vu on 1/13/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import ObjectMapper

class RegisterResult: ServiceModel {
    var registerId: String?

    override func mapping(map: Map) {
        super.mapping(map: map)
        registerId <- map["RegisterId"]
    }
}
