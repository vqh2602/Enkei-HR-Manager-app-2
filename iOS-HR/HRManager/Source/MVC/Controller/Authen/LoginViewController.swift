//
//  LoginViewController.swift
//  HRManager
//
//  Created by Tinh Vu on 1/3/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class LoginViewController: BaseVC {

    @IBOutlet weak var tfPassword: UITextField!
    @IBOutlet weak var tfPhone: UITextField!
    @IBOutlet weak var btnLogin: UIButton!
    @IBOutlet weak var btnChangePass: UIButton!

    override func viewDidLoad() {
        super.viewDidLoad()
        self.hidenNavi(true)
        self.tfPhone.keyboardType = .decimalPad

        tfPhone.placeholder = "Login.Phone".localized
        tfPassword.placeholder = "Login.Pass".localized

        btnLogin.setTitle("Login.Login".localized, for: .normal)
        btnChangePass.setTitle("Login.ChangePass".localized, for: .normal)
    }

    private func doLogin(_ phone: String, _ pass: String) {
        self.showLoading()
        APIClient.shared.login(phone, pass: pass) { (result, error) in
            self.hidenLoading()
            if let error = error {
                self.showMessage(message: error.localizedDescription)
                return
            }

            guard let response = result else {
                self.showMessage(message: "Alert.Error".localized)
                return
            }

//            if !response.successed {
//                self.showMessage(message: response.message ?? "Alert.Error".localized)
//                return
//            }

            print(response.data ?? "null")

          if response.status == 1 {
            Global.shared.saveUser(response.data)
            AppDelegate.shared.switchRootVC()
            if let deviceToken = UserDefaults.standard.string(forKey: "DeviceToken") {
              APIClient.shared.registerPushNotification(token: deviceToken, completionHandler: { (result, error) in

              })
            }
          } else if response.status == 2 {
            let changePassVC = ChangePassViewController()
            changePassVC.phone = phone
            self.navigationController?.pushViewController(changePassVC, animated: true)
          } else {
            self.showMessage(message: response.message ?? "HaveError".localized)
          }
        }
    }

    @IBAction func didTapLogin(_ sender: Any) {
        /// check valid success and login with self.phone/self.password
        self.gotoMain()
    }
    
    @IBAction func didTapChangePassword(_ sender: Any) {
        let changePassVC = ChangePassViewController()
        self.navigationController?.pushViewController(changePassVC, animated: true)
    }
    
    func gotoMain() {
        guard let phone = tfPhone.text, !phone.isEmpty else{
            self.showMessage(message: "PhoneNotEmpty".localized)
            return
        }
        guard let pass = tfPassword.text, !pass.isEmpty else{
            self.showMessage(message: "PassNotEmpty".localized)
            return
        }
        doLogin(phone, pass)
    }
}

