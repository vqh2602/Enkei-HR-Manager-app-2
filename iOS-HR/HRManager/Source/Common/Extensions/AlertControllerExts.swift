//
//  AlertControllerExts.swift
//  HRManager
//
//  Created by Tinh Vu on 1/18/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import UIKit
extension UIAlertController {
    static func showAlert(_ viewController: UIViewController, title: String? = nil, message: String, button: String = "OK".localized, handler: ((UIAlertAction) -> Swift.Void)? = nil) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: button.localized, style: .default, handler: handler))
        viewController.present(alert, animated: true, completion: nil)
    }

    static func showConfirm(_ viewController: UIViewController, title: String? = nil, message: String? = nil, okButton: String = "Bt.Yes".localized, cancelButton: String = "Bt.OK".localized, okHandler: ((UIAlertAction) -> Void)? = nil, cancelHandler: ((UIAlertAction) -> Void)? = nil) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: okButton, style: .cancel, handler: okHandler))
        alert.addAction(UIAlertAction(title: cancelButton, style: .default, handler: cancelHandler))
        viewController.present(alert, animated: true, completion: nil)
    }

    static func showActionSheet(in viewController: UIViewController, title: String? = nil, message: String? = nil, list: [String] = [], hanlder: ((Int?) -> Void)? = nil) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .actionSheet)
        for item in list {
            let action = UIAlertAction(title: item, style: .default, handler: { (action) in
                hanlder?(list.index(of: item)!)
            })
            alert.addAction(action)
        }
        let cancelAction = UIAlertAction(title: "Bt.Cancel".localized, style: .cancel) { (action) in
            hanlder?(nil)
        }
        alert.addAction(cancelAction)
        viewController.present(alert, animated: true, completion: nil)
    }
}
