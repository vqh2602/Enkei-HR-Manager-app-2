//
//  Login.swift
//  HRManager
//
//  Created by Tinh Vu on 1/13/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import ObjectMapper

class Login : ServiceModel {
    var token : String?
    var userId : String?
    var email : String?
    var fullName : String?
    var phoneNumber : String?

    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {

        token <- map["Token"]
        userId <- map["UserId"]
        email <- map["Email"]
        fullName <- map["FullName"]
        phoneNumber <- map["PhoneNumber"]
    }

}
