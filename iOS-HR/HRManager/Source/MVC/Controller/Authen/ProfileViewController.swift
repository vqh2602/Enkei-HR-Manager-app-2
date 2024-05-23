//
//  ProfileViewController.swift
//  HRManager
//
//  Created by Tinh Vu on 1/3/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class ProfileViewController: BaseVC {
    @IBOutlet weak var imvAvatar: UIImageView!
    @IBOutlet weak var lbName: UILabel!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var btnLogout: UIButton!
    @IBOutlet weak var coverHeight: NSLayoutConstraint!

    var offDay: Float = 0
    var user: User?
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hidenNavi(true)
        tableView.tableFooterView = UIView()
        btnLogout.setTitle("Profile.Logout".localized, for: .normal)

        if #available(iOS 11.0, *) {
        } else {
            coverHeight.constant = 150
        }

        getProfile()
    }

    private func getProfile() {
        self.showLoading()
        APIClient.shared.getProfile { (result, error) in
            self.hidenLoading()
            guard let response = result else {
                self.showMessage(message: "Error")
                return
            }
            self.user = response.data
            self.lbName.text = self.user?.fullName
            if let imageString = self.user?.staffImg, let imageData = Data(base64Encoded: imageString) {
                DispatchQueue.global().async {
                    let image = UIImage(data: imageData)?.fixedOrientation()
                    DispatchQueue.main.async {
                        self.imvAvatar.image = image
                    }
                }
            }
            self.tableView.reloadData()
            self.getRemainDay()
        }
    }

    private func getRemainDay() {
        APIClient.shared.getOffTime { (offDay, error) in
            if let off = offDay {
                self.offDay = off
                self.tableView.reloadData()
            }
        }
    }

    @IBAction func didTapLogout(_ sender: Any) {
      Global.shared.logout()
      AppDelegate.shared.switchRootVC()
        APIClient.shared.removeNotification { (result, error) in
        }
    }

}

extension ProfileViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 4
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style: .value1, reuseIdentifier: "Cell")
        cell.backgroundColor = .clear
      cell.textLabel?.font = UIFont(name: "ArialMT", size: 15.0)
      cell.detailTextLabel?.font = UIFont(name: "ArialMT", size: 14.0)

        switch indexPath.row {
        case 0:
            cell.imageView?.image = UIImage(named: "ic_cv")
            cell.textLabel?.text = "Profile.Position".localized
            cell.detailTextLabel?.text = user?.position
        case 1:
            cell.imageView?.image = UIImage(named: "ic_id")
            cell.textLabel?.text = "Profile.StaffCode".localized
            cell.detailTextLabel?.text = user?.staffCode
        case 2:
            cell.imageView?.image = UIImage(named: "ic_phone")
            cell.textLabel?.text = "Profile.Phone".localized
            cell.detailTextLabel?.text = user?.mobile
        case 3:
            cell.imageView?.image = UIImage(named: "ic_sncl")
            cell.textLabel?.text = "Profile.RemainDay".localized
            cell.detailTextLabel?.text = "\(offDay)"
        default: break
            
        }
        return cell
    }
}
