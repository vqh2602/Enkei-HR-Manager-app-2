//
//  ChangePassViewController.swift
//  HRManager
//
//  Created by Tinh Vu on 1/3/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class ChangePassViewController: BaseVC {
    @IBOutlet weak var lbTitle: UILabel!
    @IBOutlet weak var tfPhone: UITextField!
    @IBOutlet weak var tfOldPass: UITextField!
    @IBOutlet weak var tfNewPass: UITextField!
    @IBOutlet weak var tfConfirmPass: UITextField!
    @IBOutlet weak var btnSave: UIButton!

    var phone: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hidenNavi(true)
        self.lbTitle.text = "Register.Title".localized
        tfPhone.placeholder = "Login.Phone".localized
        tfOldPass.placeholder = "Register.OldPass".localized
        tfNewPass.placeholder = "Register.NewPass".localized
        tfConfirmPass.placeholder = "Register.ConfirmPass".localized
        if let phone = self.phone {
            tfPhone.text = phone
        }
        btnSave.setTitle("Register.Save".localized, for: .normal)
    }

    private func changePass(phone: String, oldass: String, newpass: String) {
        self.showLoading()
        APIClient.shared.changePass(phone, oldpass: oldass, newpass: newpass) { (result, error) in
            self.hidenLoading()
            if let error = error {
                self.showMessage(message: error.localizedDescription)
                return
            }

            if let response = result, response.successed {
                self.showMessage(message: "ChangePassSuccess".localized, handler: {
                    self.goBack()
                })
            } else {
                self.showMessage(message: result?.message)
            }
        }
    }

    private func goBack() {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func didTapBack(_ sender: Any) {
        goBack()
    }
    
    @IBAction func didTapSave(_ sender: Any) {
        guard let phone = tfPhone.text, !phone.isEmpty else{
            self.showMessage(message: "PhoneNotEmpty".localized)
            return
        }
        guard let oldpass = tfOldPass.text, !oldpass.isEmpty else{
            self.showMessage(message: "OldPassNotEmpty".localized)
            return
        }
        guard let newpass = tfNewPass.text, !newpass.isEmpty else{
            self.showMessage(message: "NewPassNotEmpty".localized)
            return
        }
        guard let confirmpass = tfConfirmPass.text, !confirmpass.isEmpty else{
            self.showMessage(message: "ConfirmPassNotEmpty".localized)
            return
        }
        guard newpass == confirmpass else {
            self.showMessage(message: "ConfirmPassNotMatch".localized)
            return
        }
        changePass(phone: phone, oldass: oldpass, newpass: newpass)
    }
}
