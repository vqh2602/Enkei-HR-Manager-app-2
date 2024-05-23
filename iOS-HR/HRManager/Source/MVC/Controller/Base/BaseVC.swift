//
//  BaseVC.swift
//  BaseProject
//
//  Created by Tinh Vu on 7/29/18.
//  Copyright © 2018 alatin studio. All rights reserved.
//

import Foundation
import UIKit
import MBProgressHUD

class BaseVC: UIViewController {
    
    func hidenNavi(_ hidden: Bool) {
        self.navigationController?.isNavigationBarHidden = hidden
    }

    func showLoading() {
        let hud = MBProgressHUD.showAdded(to: self.view, animated: true)
        hud.label.text = "Loading".localized

    }

    func hidenLoading() {
        MBProgressHUD.hide(for: self.view, animated: true)
    }
}
