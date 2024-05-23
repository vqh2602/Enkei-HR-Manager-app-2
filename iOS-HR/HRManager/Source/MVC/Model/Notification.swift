//
//  Notification.swift
//  HRManager
//
//  Created by Tinh Vu on 1/16/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import ObjectMapper

class Notification : ServiceModel {
  var id : Int?
  var notificationCampaignId : Int?
  var userId : String?
  var readStatus : Bool?
  var lastModifiedOnDate : String?
  var title : String?
    var registerId: String?

  required init?(map: Map) {
    super.init(map: map)
  }

    var ago: String {
        guard let modified = self.lastModifiedOnDate, let date = modified.toDate() else {
            return "Undefined".localized
        }
        let range = (Date()).timeIntervalSince1970 - date.timeIntervalSince1970
        if range < 0 {
            return "Undefined".localized
        }
        if range <= 60 { // 1 minute
            return "1 " + "MinAgo".localized
        } else if range <= 60*60 { // 1 hour
            let time: Int = Int(ceil(range / 60))
            return "\(time) " + "MinAgo".localized
        } else if range <= 60*60*24 { // 1 day
            let time: Int = Int(ceil(range / (60*60)))
            return "\(time) " + "HourAgo".localized
        } else if range <= 60*60*24*30 { // 1 month
            let time: Int = Int(ceil(range / (60*60*24)))
            return "\(time) " + "DayAgo".localized
        } else if range <= 60*60*24*30*12 { // 1 year
            let time: Int = Int(ceil(range / (60*60*24*30)))
            return "\(time) " + "MonthAgo".localized
        } else {
            let time: Int = Int(ceil(range / (60*60*24*30*12)))
            return "\(time) " + "YearAgo".localized
        }
    }

  override func mapping(map: Map) {

    id <- map["Id"]
    notificationCampaignId <- map["NotificationCampaignId"]
    userId <- map["UserId"]
    readStatus <- map["ReadStatus"]
    lastModifiedOnDate <- map["LastModifiedOnDate"]
    title <- map["Title"]
    registerId <- map["RegisterId"]
  }

}
