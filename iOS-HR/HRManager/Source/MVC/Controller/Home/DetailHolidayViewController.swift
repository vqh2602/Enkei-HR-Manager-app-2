//
//  DetailHolidayViewController.swift
//  HRManager
//
//  Created by Tienvv on 15/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class DetailHolidayViewController: BaseVC {

    @IBOutlet weak var lblTitleScreen: UILabel!
    @IBOutlet weak var tableView: UITableView!

    @IBOutlet weak var rightView: UIView!
    @IBOutlet weak var leftView: UIView!
    @IBOutlet weak var centerView: UIView!
    @IBOutlet weak var rightBtn: UIButton!
    @IBOutlet weak var leftButton: UIButton!
    @IBOutlet weak var centerButton: UIButton!
    @IBOutlet weak var topViewTop: NSLayoutConstraint!
    @IBOutlet weak var actionViewBottom: NSLayoutConstraint!
    

    var event: Event?
    var workflow: [String] = []
    var registerId: String?

    let titlesFull = ["Detail.Position", "Detail.StaffCode", "Detail.Phone", "Detail.LeaveType", "Detail.OffDays", "Detail.CountDay", "Detail.Accepter"]
    let titles = ["Detail.LeaveType", "Detail.OffDays", "Detail.CountDay", "Detail.Accepter"]
    override func viewDidLoad() {
        super.viewDidLoad()
        self.lblTitleScreen.text = "Detail.Title".localized
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.register(UINib(nibName: AvatarTableViewCell.id(), bundle: nil), forCellReuseIdentifier: AvatarTableViewCell.id())
        self.tableView.register(UINib(nibName: "DetailHolidayCell", bundle: nil), forCellReuseIdentifier: "DetailHolidayCell")
        self.tableView.register(UINib(nibName: ActionRegisterHollidayCell.id(), bundle: nil), forCellReuseIdentifier: ActionRegisterHollidayCell.id())

        self.tableView.tableFooterView = UIView()

        if #available(iOS 11.0, *) {
        } else {
            actionViewBottom.constant = 50
            topViewTop.constant = 20
            view.updateConstraints()
        }

      getEventDetail()
    }

   func getEventDetail() {
    if let id = registerId {
      self.showLoading()
      APIClient.shared.getEventDetail(registerId: id) { (result, error) in
        self.hidenLoading()
        guard let response = result else {
          return
        }
        self.event = response.data
        self.tableView.reloadData()
        self.getNextFlow()
      }
    } else {
      getNextFlow()
    }
  }

    private func getNextFlow() {
        guard let id = event?.registerId else {
            return
        }
        self.showLoading()
        APIClient.shared.getWorkFlow(registerId: id) { (flow, error) in
            self.hidenLoading()
            if let workflow = flow {
                self.workflow = workflow
                self.swithButtonStatus()
            }
        }
    }

    private func processing(id: String?, comment: String, command: String) {
        guard let registerId = id else { return }
        self.showLoading()
        APIClient.shared.process(registerId: registerId, comment: comment, command: command) { (result, error) in
            self.hidenLoading()
            guard let response = result else {
                self.showMessage(message: "HaveError".localized)
                return
            }
            if response.successed {
                self.gotoSuccess()
            }
        }
    }

    private func deleteEvent() {
        guard let id = event?.registerId else {
            return
        }
        self.showLoading()
        APIClient.shared.deleteEvent(registerId: id) { (result, error) in
            self.hidenLoading()
            if let response = result, response.successed {
                self.gotoSuccess()
            }
        }
    }

    private func swithButtonStatus() {

        if let state = event?.state, state == .Draft {
            self.workflow.append("Edit")
        }
        // Deprecated Stop
        if let index = self.workflow.indexes(of: "Stop").first {
          self.workflow.remove(at: index)
        }
        if self.workflow.count == 0 {
            centerView.isHidden = true
            leftView.isHidden = true
            rightBtn.isHidden = true
        } else if self.workflow.count == 1 {
            leftView.isHidden = true
            rightView.isHidden = true
            centerView.isHidden = false
            if let flowProcess1 = Processing(rawValue: self.workflow[0]) {
                centerButton.setTitle(flowProcess1.description, for: .normal)
            }
        } else {
            centerView.isHidden = true
            leftView.isHidden = false
            rightView.isHidden = false

            if self.workflow.contains("Edit") {
                if let flowProcess1 = Processing(rawValue: self.workflow[0]) {
                    leftButton.setTitle(flowProcess1.description, for: .normal)
                    rightBtn.setTitle("Edit".localized, for: .normal)
                }
            } else {
                if let flowProcess1 = Processing(rawValue: self.workflow[0]), let flowProcess2 = Processing(rawValue: self.workflow[1]) {
                    leftButton.setTitle(flowProcess1.description, for: .normal)
                    rightBtn.setTitle(flowProcess2.description, for: .normal)
                }
            }

        }
    }

    private func gotoSuccess() {
        let vc = CreatedSuccessViewController()
        self.navigationController?.pushViewController(vc, animated: true)
    }

    @IBAction func didTapBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

    @IBAction func didTapLeft(_ sender: Any) {
        doProcessing(command: workflow[0])
    }
    
    @IBAction func didTapCenter(_ sender: Any) {
        doProcessing(command: workflow[0])
    }
    
    @IBAction func didTapRight(_ sender: Any) {
        doProcessing(command: workflow[1])
    }
}

extension DetailHolidayViewController: UITableViewDelegate, UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 1
        }
        if section == 2 {
            return 2
        }
        guard let leftId = Global.shared.user?.userId, let rightId = event?.createdByUser?.userId else {
            return 0
        }
        return leftId == rightId ? titles.count : titlesFull.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: AvatarTableViewCell.id()) as! AvatarTableViewCell
            cell.fillData(event: event)
            return cell
        } else if indexPath.section == 1 {
            var cell = tableView.dequeueReusableCell(withIdentifier: "Cell")
            if cell == nil {
                cell = UITableViewCell(style: .value1, reuseIdentifier: "Cell")
            }
            cell?.selectionStyle = .none
            cell?.textLabel?.font = UIFont(name: "ArialMT", size: 15.0)
            cell?.detailTextLabel?.font = UIFont(name: "ArialMT", size: 14.0)
            if let leftId = Global.shared.user?.userId, let rightId = event?.createdByUser?.userId {
                if leftId == rightId {
                    cell?.textLabel?.text = titles[indexPath.row].localized
                    switch indexPath.row {
                    case 0:
                        cell?.detailTextLabel?.text = event?.leaveTypeName
                        break
                    case 1:
                        if let startDate = event?.startDate, let endDate = event?.endDate {
                            cell?.detailTextLabel?.text = "\(startDate.toString()) - \(endDate.toString())"
                        }
                        break
                    case 2:
                        cell?.detailTextLabel?.text = "\(event?.countDay ?? 0)"
                        break
                    case 3:
                        cell?.detailTextLabel?.text = event?.transitionUser?.userId == event?.createdByUser?.userId ? "" : event?.transitionUser?.fullName
                        break

                    default:
                        break
                    }
                } else {
                    cell?.textLabel?.text = titlesFull[indexPath.row].localized
                    switch indexPath.row {
                    case 0:
                        cell?.detailTextLabel?.text = event?.createdByUser?.position
                        break
                    case 1:
                        cell?.detailTextLabel?.text = event?.createdByUser?.staffCode
                        break
                    case 2:
                        cell?.detailTextLabel?.text = event?.createdByUser?.mobile
                        break
                    case 3:
                        cell?.detailTextLabel?.text = event?.leaveTypeName
                        break
                    case 4:
                        if let startDate = event?.startDate, let endDate = event?.endDate {
                            cell?.detailTextLabel?.text = "\(startDate.toString()) - \(endDate.toString())"
                        }
                        break
                    case 5:
                        cell?.detailTextLabel?.text = "\(event?.countDay ?? 0)"
                        break
                    case 6:
                        cell?.detailTextLabel?.text = event?.transitionUser?.userId == event?.createdByUser?.userId ? "" : event?.transitionUser?.fullName
                        break

                    default:
                        break
                    }
                }
            }
            return cell!
        } else if indexPath.section == 2 {
            let cell = UITableViewCell(style: .subtitle, reuseIdentifier: "NoteCell")
            cell.selectionStyle = .none
            cell.textLabel?.font = UIFont(name: "ArialMT", size: 15.0)
            cell.detailTextLabel?.font = UIFont(name: "ArialMT", size: 14.0)
            cell.detailTextLabel?.numberOfLines = 0
            cell.detailTextLabel?.textColor = .lightGray
            if indexPath.row == 0 {
                cell.textLabel?.text = "Detail.Content".localized
                cell.detailTextLabel?.text = event?.des
            } else {
                cell.textLabel?.text = "Comment"
                cell.detailTextLabel?.text = event?.comment
            }
            return cell
        }
        return UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section != 1 {
            return 100
        }
        return 50
    }
}

extension DetailHolidayViewController {
    func doProcessing(command: String) {
      if command == Processing.StartProcessing.rawValue {
        self.processing(id: self.event?.registerId, comment: "", command: command)
      } else if command == "Edit" {
        let editVC = RegisterHolidayViewController()
        editVC.event = self.event
        self.navigationController?.pushViewController(editVC, animated: true)
      } else {
        self.showInputDialog(title: "EnterComment".localized, cancelHandler: { (_) in
        }) { (string) in
          guard let string = string else {
            self.showMessage(message: "EnterCommentContent".localized)
            return
          }
          self.processing(id: self.event?.registerId, comment: string, command: command)
        }
      }

    }
}
