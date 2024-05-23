//
//  Enum.swift
//  HRManager
//
//  Created by Tinh Vu on 1/13/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation

enum WorkFlow: String, CustomStringConvertible {
  
    case Draft = "Draft"
    case QLC1Execute = "QLC1Execute"
    case QLC2Execute = "QLC2Execute"
    case HRExecute = "HRExecute"
    case StoreContent = "StoreContent"
    case DenialContent = "DenialContent"

    var description: String {
        return self.rawValue.localized
    }

    var color: String {
        switch (self) {
        case .Draft: return "#9ea0a5"
        case .QLC1Execute, .QLC2Execute, .HRExecute: return "#2c66ef"
        case .StoreContent: return "#4ece3d"
        default:
            return "#ff151f"
        }
    }

    var deleteable: Bool {
        switch self {
        case .Draft, .DenialContent:
            return true
        default:
            return false
        }
    }

    var isDenial: Bool {
        switch self {
        case .DenialContent:
            return true
        default:
            return false
        }
    }
}

enum Processing: String, CustomStringConvertible {
    case StartProcessing = "StartProcessing"
    case Denial = "Denial"
    case Approve = "Approve"
    case Publish = "Publish"

    var description: String {
        return "Processing.\(self.rawValue)".localized
    }
}

enum EventStatus: String, CustomStringConvertible {
    case Draft = "Draft"
    case Accepted = "Approve"
    case Rejected = "Denial"
    case Done = "Publish"

    var description: String {
        return self.rawValue.localized
    }

    var color: String {
        switch (self) {
        case .Draft: return "#9ea0a5"
        case .Accepted: return "#2c66ef"
        case .Rejected: return "#ff151f"
        case .Done: return "#4ece3d"
        }
    }
}
