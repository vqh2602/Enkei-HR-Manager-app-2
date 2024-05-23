//
//  Global.swift
//  HRManager
//
//  Created by Tienvv on 13/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import UIKit

class Global {
    static let shared = Global()
    
    var user: Login?

    var isLogined: Bool {
        return user != nil
    }

    var offDates: [Date?] {
        return offDays.map{ serverDateFormatter.date(from: $0) }
    }

    var offDays: [String] = []

    lazy var localDateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.dateFormat = "dd/MM/yyyy"
        return df
    }()

    lazy var serverDateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        return df
    }()

    lazy var minimumDate: Date? = {
        let currentDate = Date()
        let currentDateString = self.localDateFormatter.string(from: currentDate)
        let components = currentDateString.components(separatedBy: "/")
        let minumumDateString = "1/1/\(components[2])"
        return self.localDateFormatter.date(from: minumumDateString)
    }()

    lazy var maximumDate: Date? = {
        let currentDate = Date()
        let currentDateString = self.localDateFormatter.string(from: currentDate)
        let components = currentDateString.components(separatedBy: "/")
        var maximumDateString = ""
        let day = Int(components[0])
        let month = Int(components[1])
        let year = Int(components[2])
        if day! > 23, month! == 12 {
            maximumDateString = "31/12/\(year! + 1)"
        } else {
            maximumDateString = "31/12/\(year!)"
        }
        return self.localDateFormatter.date(from: maximumDateString)
    }()

    init() {
        loadData()
    }

    func saveUser(_ user: Login?) {
        self.user = user
        let json = user?.toJSON()

        UserDefaults.standard.set(json, forKey: "User")
    }

    func loadData() {
        if let json = UserDefaults.standard.value(forKey: "User") as? [String: Any] {
            self.user = Login(JSON: json)
        }
    }

    func logout() {
        self.saveUser(nil)
    }

    func fakeAvatar(userId: String? = nil) -> UIImage? {
        var id = userId
        if id == nil {
            id = self.user?.userId
        }
        var imageName = "ndl"
        guard let userId = id else {
            return UIImage(named: imageName)
        }
        switch userId.uppercased() {
        case "A8C50D11-CAA0-4980-A9F0-485010B99415": imageName = "ndl"
        case "39BF116C-6763-4F9C-8C9C-A664D50387BF": imageName = "lvt"
        case "886A5B5A-7378-4DD0-AFC8-DE3E1217E9FD": imageName = "mmc"
        case "ABA0613B-6F44-4201-A2A9-4B850F5FCE76": imageName = "ttmh"
        default: imageName = "ndl"
        }
        return UIImage(named: imageName)
    }

    func stringify(json: Any, prettyPrinted: Bool = false) -> String {
        var options: JSONSerialization.WritingOptions = []
        if prettyPrinted {
            options = JSONSerialization.WritingOptions.prettyPrinted
        }

        do {
            let data = try JSONSerialization.data(withJSONObject: json, options: options)
            if let string = String(data: data, encoding: String.Encoding.utf8) {
                return string
            }
        } catch {
            print(error)
        }

        return ""
    }
}
