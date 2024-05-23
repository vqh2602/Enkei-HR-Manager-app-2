//
//  ListEventViewController.swift
//  HRManager
//
//  Created by Tinh Vu on 3/7/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class ListEventViewController: BaseVC {
    @IBOutlet weak var lbTitle: UILabel!
    @IBOutlet weak var tableView: UITableView!
    var events: [Event] = []
    var date: Date = Date()

    override func viewDidLoad() {
        super.viewDidLoad()
        self.lbTitle.text = date.toString(dateFormat: "dd/MM/yyyy")
        self.tableView.register(UINib(nibName: HomeCell.id(), bundle: nil), forCellReuseIdentifier: HomeCell.id())
        self.tableView.tableFooterView = UIView()
    }

    @IBAction func didTapBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

}

extension ListEventViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return events.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "HomeCell", for: indexPath) as! HomeCell
        cell.fillData(event: events[indexPath.row])
        return cell
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if #available(iOS 11, *) {
            return 196.0
        } else {
            return 216.0
        }
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let event = events[indexPath.row]
        let vc: DetailHolidayViewController = DetailHolidayViewController()
        vc.event = event
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
