//
//  DateExts.swift
//  HRManager
//
//  Created by Tinh Vu on 1/14/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation

extension Date {
    func toOriginal() -> Date? {
        let currentString = Global.shared.serverDateFormatter.string(from: self)
        let components = currentString.components(separatedBy: "T")
        let targetString = "\(components[0])T00:00:00"
        return Global.shared.serverDateFormatter.date(from: targetString)
    }
}

extension Date {
    func toString( dateFormat format  : String ) -> String
    {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: self)
    }

    func compareDate(with: Date) -> Bool {
        return Calendar.current.compare(self, to: with, toGranularity: .day) == .orderedSame
    }
}
