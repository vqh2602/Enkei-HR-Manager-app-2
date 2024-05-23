//
//  NotificationViewController.swift
//  HRManager
//
//  Created by Tienvv on 13/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import UIKit

class NotificationViewController: BaseVC {
    
    @IBOutlet weak var lbTitle: UILabel!
    @IBOutlet weak var tableView: UITableView!

    @IBOutlet weak var topViewTop: NSLayoutConstraint!

    var items: [Notification] = []
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(UINib(nibName: NotificationTableViewCell.id(), bundle: nil), forCellReuseIdentifier: NotificationTableViewCell.id())
        tableView.separatorStyle = .none
        self.hidenNavi(true)
        self.lbTitle.text = "Notification.Title".localized

        if #available(iOS 11, *) {

        }else {
            topViewTop.constant = 20
        }
        
    }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    getData()
  }

      private func getData() {
        self.showLoading()
        APIClient.shared.getNotifications { (result, error) in
          self.hidenLoading()
          guard let response = result, let notifications = response.dataList else { return }
          self.items = notifications
          self.tableView.reloadData()
        }
      }
}

extension NotificationViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: NotificationTableViewCell.id()) as! NotificationTableViewCell
      cell.fillData(notifi: items[indexPath.row])
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let detailVC = DetailHolidayViewController()
        detailVC.registerId = items[indexPath.row].registerId
        detailVC.getEventDetail()
        self.navigationController?.pushViewController(detailVC, animated: true)
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableView.automaticDimension
    }
}
