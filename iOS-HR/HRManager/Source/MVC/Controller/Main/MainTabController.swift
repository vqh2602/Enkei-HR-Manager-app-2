//
//  MainTabController.swift
//  HRManager
//
//  Created by Tienvv on 13/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import UIKit

class MainTabController: UITabBarController {
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupTabbar()
    }
    
    func setupTabbar() {
        let homeVC = UINavigationController(rootViewController: HomeViewController())
        homeVC.tabBarItem.image = UIImage(named: "ic_home")
        
        let notifiVC = UINavigationController(rootViewController: NotificationViewController())
        notifiVC.tabBarItem.image = UIImage(named: "ic_notification")

        let calendarVC = UINavigationController(rootViewController: CalendarViewController())
        calendarVC.isNavigationBarHidden = true
        calendarVC.tabBarItem.image = UIImage(named: "ic_calendar")
        
        let profileVC = UINavigationController(rootViewController: ProfileViewController())
        profileVC.tabBarItem.image = UIImage(named: "ic_profile")
        
        self.viewControllers = [homeVC, notifiVC, calendarVC, profileVC]
    }
}
