//
//  AppDelegate.swift
//  HRManager
//
//  Created by Tinh Vu on 1/3/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit
import IQKeyboardManagerSwift
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    
    private static var sharedInstance: AppDelegate?
    static var shared: AppDelegate {
        return sharedInstance!
    }


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        AppDelegate.sharedInstance = self
        IQKeyboardManager.shared.enable = true
        IQKeyboardManager.shared.enabledTouchResignedClasses = [BaseVC.self]
        window = UIWindow(frame: UIScreen.main.bounds)
        switchRootVC()

        initNotification()

        if let notification = launchOptions?[.remoteNotification] as? [String: AnyObject] {
            let data = notification["data"] as! [String: AnyObject]

            if let id = data["id"] as? String {
                DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.5) {
                    self.handlerNotification(withId: id, in: application)
                }
            }
        }

        APIClient.shared.getOffDays(year: "\(Date().year)") { (dates, error) in
            if let dates = dates {
                Global.shared.offDays = dates
            }
        }

        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        application.applicationIconBadgeNumber = 0
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }

    func initNotification() {
        registerForPushNotifications()
    }

    func registerForPushNotifications() {
        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) {
                (granted, error) in
                print("Permission granted: \(granted)")
                guard granted else { return }
                DispatchQueue.main.async {
                    self.getNotificationSettings()
                }
            }
            UNUserNotificationCenter.current().delegate = self
        } else {
            let notificationTypes: UIUserNotificationType = [UIUserNotificationType.alert, UIUserNotificationType.badge, UIUserNotificationType.sound]
            let pushNotificationSettings = UIUserNotificationSettings(types: notificationTypes, categories: nil)
            UIApplication.shared.registerUserNotificationSettings(pushNotificationSettings)
            UIApplication.shared.registerForRemoteNotifications()
        }
    }

    func getNotificationSettings() {
        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().getNotificationSettings { (settings) in
                print("Notification settings: \(settings)")
                guard settings.authorizationStatus == .authorized else { return }
                DispatchQueue.main.async {
                    UIApplication.shared.registerForRemoteNotifications()
                }
            }
        } else {
            // Fallback on earlier versions
        }
    }

    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) {
        guard application.applicationState != .active else {
            return
        }

        print(userInfo)
        guard let data = userInfo["data"] as? [String: AnyObject] else { return }
        print(data)

        if let id = data["id"] as? String {
            handlerNotification(withId: id, in: application)
        }

    }


    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenParts = deviceToken.map { data -> String in
            return String(format: "%02.2hhx", data)
        }

        let token = tokenParts.joined()
        print("Device Token: \(token)")
        UserDefaults.standard.set(token, forKey: "DeviceToken")

    }

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {

    }

    private func handlerNotification(withId id: String, in application: UIApplication) {
        guard let visibleVC = application.visibleViewController else {
            return
        }

      if visibleVC is DetailHolidayViewController {
        let detailVC = visibleVC as! DetailHolidayViewController
        detailVC.registerId = id
        detailVC.getEventDetail()
        return
      }

      let detailVC = DetailHolidayViewController()
      detailVC.registerId = id
      visibleVC.navigationController?.pushViewController(detailVC, animated: true)

    }

}

extension AppDelegate: UNUserNotificationCenterDelegate {
    @available(iOS 10.0, *)
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.alert, .badge, .sound])
    }
}


extension UIApplication {
  var visibleViewController: UIViewController? {

    guard let rootViewController = keyWindow?.rootViewController else {
      return nil
    }

    return getVisibleViewController(rootViewController)
  }

  private func getVisibleViewController(_ rootViewController: UIViewController) -> UIViewController? {

    if let presentedViewController = rootViewController.presentedViewController {
      return getVisibleViewController(presentedViewController)
    }

    if let navigationController = rootViewController as? UINavigationController {
      return getVisibleViewController(navigationController.visibleViewController!)
    }

    if let tabBarController = rootViewController as? UITabBarController {
      return getVisibleViewController(tabBarController.selectedViewController!)
    }

    return rootViewController
  }
  
}

extension AppDelegate {
    func switchRootVC() {
        if Global.shared.isLogined {
            let mainTabVC = MainTabController()
            window?.rootViewController = mainTabVC
            window?.makeKeyAndVisible()
        } else {
            let authenNav = BaseNavVC(rootViewController: LoginViewController())
            window?.rootViewController = authenNav
            window?.makeKeyAndVisible()
        }
    }
}
