//
//  APIClient.swift
//  HRManager
//
//  Created by Tinh Vu on 1/13/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import Alamofire
import ObjectMapper

class APIClient: NSObject {
    static let shared = APIClient()
    let manager: SessionManager!

    private override init() {
        let configuration = URLSessionConfiguration.default
        configuration.timeoutIntervalForRequest = 60 //seconds
        configuration.timeoutIntervalForResource = 60
        manager = Alamofire.SessionManager(configuration:configuration)
    }

    private func request<T: ServiceModel>(url: String, params: [String: Any]? = nil, method: HTTPMethod = .get, completionHandler: @escaping(_ response: APIResult<T>?, _ error: Error?) -> Void) {
        manager.request(url, method: method, parameters: params).responseJSON { (response) in
            print("Result: \(response.debugDescription)")
            if let json = response.result.value as? [String: Any] {
                let data = APIResult<T>(json)
                completionHandler(data, nil)
            } else {
                completionHandler(nil, response.error)
            }
        }
    }


    func login(_ phone: String, pass: String, completionHandler: @escaping(_ response: APIResult<Login>?, _ error: Error?) -> Void) {
        let path = "account/login"
        let url = Config.BASE_URL + path
        #if DEBUG
        let sandbox = 1
        #else
        let sandbox = 0
        #endif
        let params = ["UserName": phone,
                      "PassWord": pass,
                      "SandBox": sandbox] as [String : Any]
        request(url: url, params: params, method: .post, completionHandler: completionHandler)
    }

    func changePass(_ phone: String, oldpass: String, newpass: String, completionHandler: @escaping(_ response: APIResult<Login>?, _ error: Error?) -> Void) {
        let path = "account/change-password"
        let url = Config.BASE_URL + path
        let params = ["UserName": phone,
                      "PassWord": newpass,
                      "OldPassWord": oldpass]
        request(url: url, params: params, method: .put, completionHandler: completionHandler)
    }

    func getLeaveType(completionHandler: @escaping(_ response: APIResult<LeaveType>?, _ error: Error?) -> Void) {
        let path = "loainghiphep/all"
        let url = Config.BASE_URL + path
        request(url: url, completionHandler: completionHandler)
    }

    func getEvents(_ filter: EventFilter? = nil, completionHandler: @escaping(_ response: APIResult<Event>?, _ error: Error?) -> Void) {
        let path = "quanlynghiphep/list"
        var params: [String: Any] = [:]
        if let filter = filter {
            params = filter.toDict()
        }
        let url = Config.BASE_URL + path
        manager.request(url, method: .post, parameters: params, encoding: JSONEncoding.default).responseJSON { (response) in
        print("Result: \(response.debugDescription)")
        if let json = response.result.value as? [String: Any] {
          let data = APIResult<Event>(json)
          completionHandler(data, nil)
        } else {
          completionHandler(nil, response.error)
        }
      }
    }

    func getAllEvents(completionHandler: @escaping(_ response: APIResult<Event>?, _ error: Error?) -> Void) {
        let path = "quanlynghiphep/calendar"
        guard let userId = Global.shared.user?.userId else { return }
        let params = ["LoginUserId": userId]

        let url = Config.BASE_URL + path
        manager.request(url, method: .post, parameters: params, encoding: JSONEncoding.default).responseJSON { (response) in
            print("Result: \(response.debugDescription)")
            if let json = response.result.value as? [String: Any] {
                let data = APIResult<Event>(json)
                completionHandler(data, nil)
            } else {
                completionHandler(nil, response.error)
            }
        }
    }

    func getOffDays(year: String, completionHandler: @escaping(_ response: [String]?, _ error: Error?) -> Void) {
        let path = "quanlynghiphep/leaveday/\(year)"

        let url = Config.BASE_URL + path
        manager.request(url, method: .get).responseJSON { (response) in
            print("Result: \(response.debugDescription)")
            if let json = response.result.value as? [String: Any], let data = json["Data"] as? [String] {
                completionHandler(data, nil)
            } else {
                completionHandler(nil, response.error)
            }
        }
    }

  func getEventDetail(registerId: String, completionHandler: @escaping(_ response: APIResult<Event>?, _ error: Error?) -> Void) {
    let path = "quanlynghiphep/\(registerId)"
    let url = Config.BASE_URL + path
    request(url: url, completionHandler: completionHandler)
  }

    func getWorkFlow(registerId: String, completionHandler: @escaping(_ response: [String]?, _ error: Error?) -> Void) {
        guard let userId = Global.shared.user?.userId else { return }
        let path = "workflow/documents/\(registerId)/availablecommands?userId=\(userId)"
        let url = Config.BASE_URL + path
        manager.request(url).responseJSON { (response) in
            print("Result: \(response.debugDescription)")
            if let json = response.result.value as? [String: Any] {
                if let data = json["Data"] as? [String] {
                    completionHandler(data, nil)
                } else {
                    completionHandler(nil, response.error)
                }
            } else {
                completionHandler(nil, response.error)
            }
        }
    }

    func registerEvent(model: RegisterHolliday, completionHandler: @escaping(_ response: APIResult<RegisterResult>?, _ error: Error?) -> Void) {
        let path = "quanlynghiphep"
        let url = Config.BASE_URL + path

        request(url: url, params: model.toDict(), method: .post, completionHandler: completionHandler)
    }

    func updateEvent(model: EditHoliday, completionHandler: @escaping(_ response: APIResult<RegisterResult>?, _ error: Error?) -> Void) {
        let path = "quanlynghiphep/\(model.registerId)"
        let url = Config.BASE_URL + path

        request(url: url, params: model.toDict(), method: .put, completionHandler: completionHandler)
    }

    func deleteEvent(registerId: String, completionHandler: @escaping(_ response: APIResult<RegisterResult>?, _ error: Error?) -> Void) {
        let path = "quanlynghiphep/\(registerId)"
        let url = Config.BASE_URL + path
        request(url: url, method: .delete, completionHandler: completionHandler)
    }

    func process(registerId: String, comment: String, command: String, completionHandler: @escaping(_ response: APIResult<ProcessResult>?, _ error: Error?) -> Void) {
        guard let userId = Global.shared.user?.userId else { return }
        let path = "quanlynghiphep/workflow/\(registerId)/process"
        let url = Config.BASE_URL + path
        let params = ["DocumentId": registerId,
                      "Command": command,
                      "Comment": comment,
                      "UserId": userId,
                      "ActorId": userId,
                      ] as [String : Any]
        request(url: url, params: params, method: .post, completionHandler: completionHandler)
    }

    func registerPushNotification(token: String, completionHandler: @escaping(_ response: APIResult<ProcessResult>?, _ error: Error?) -> Void) {
        guard let userId = Global.shared.user?.userId else { return }
        let path = "notifications/register-device-token"
        let url = Config.BASE_URL + path
        let params = ["DeviceId": UIDevice.current.identifierForVendor?.uuidString ?? "",
                      "UserId": userId,
                      "DeviceToken": token,
                      "DeviceType": "iOS",
                      ] as [String : Any]
        request(url: url, params: params, method: .post, completionHandler: completionHandler)
    }

    func removeNotification(completionHandler: @escaping(_ response: APIResult<ProcessResult>?, _ error: Error?) -> Void) {
        guard let deviceToken = UserDefaults.standard.string(forKey: "DeviceToken") else {
            completionHandler(nil, nil)
            return
        }
        let path = "notifications/unregister-device-token"
        let url = Config.BASE_URL + path
        let params = ["DeviceToken": deviceToken,
                      ] as [String : Any]
        request(url: url, params: params, method: .put, completionHandler: completionHandler)
    }

    func getNotifications(completionHandler: @escaping(_ response: APIResult<Notification>?, _ error: Error?) -> Void) {
      guard let userId = Global.shared.user?.userId else { return }
      let path = "notifications/\(userId)"
      let url = Config.BASE_URL + path
      request(url: url, completionHandler: completionHandler)
    }

    func getOffTime(completionHandler: @escaping(_ response: Float?, _ error: Error?) -> Void) {
        guard let userId = Global.shared.user?.userId else { return }
        let path = "quanlynghiphep/dayoffremain/query"
        let url = Config.BASE_URL + path
        let params = [
                      "RegisterUserId": userId,
                      ] as [String : Any]
        manager.request(url, method: .post, parameters: params).responseJSON { (response) in
            print("Result: \(response.debugDescription)")
            if let json = response.result.value as? [String: Any] {
                if let data = json["Data"] as? Float {
                    completionHandler(data, nil)
                } else {
                    completionHandler(nil, response.error)
                }
            } else {
                completionHandler(nil, response.error)
            }
        }
    }

    func getProfile(_ userId: String? = nil, completionHandler: @escaping(_ response: APIResult<User>?, _ error: Error?) -> Void) {
        var userId = userId
        if userId == nil {
            userId = Global.shared.user?.userId
        }
        let path = "account/profile/\(userId ?? "")"
        let url = Config.BASE_URL + path
        request(url: url, completionHandler: completionHandler)
    }
}
