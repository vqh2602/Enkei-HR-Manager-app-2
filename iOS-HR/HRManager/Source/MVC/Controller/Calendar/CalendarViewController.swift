//
//  CalendarViewController.swift
//  HRManager
//
//  Created by Tinh Vu on 3/18/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class CalendarViewController: BaseVC {
    @IBOutlet weak var calendar: FSCalendar!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var calendarHeightConstraint: NSLayoutConstraint!

    private var filterModel = EventFilter(userId: Global.shared.user?.userId)
    private var events: [Event] = []
    private var eventSelecteds: [Event] = []
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(UINib(nibName: HomeCell.id(), bundle: nil), forCellReuseIdentifier: HomeCell.id())
        tableView.separatorStyle = .none
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        getEvents()
    }

    private func offDays() {
        APIClient.shared.getOffDays(year: "\(Date().year)") { (dates, error) in
            if let dates = dates {
            }
        }
    }

    private func getEvents() {
        self.showLoading()
        APIClient.shared.getAllEvents { (events, error) in
            self.hidenLoading()
            guard let response = events, let data = response.dataList else {
                self.showMessage(message: "Error")
                return
            }
            self.events = data
            self.calendar.reloadData()
        }
    }

    private func isEvent(_ event: Event, date: Date) -> Bool {
        guard let createdDate = event.createdDate, let endedDate = event.endedDate else {
            return false
        }
        if createdDate.compareDate(with: date) || endedDate.compareDate(with: date) || ((date.timeIntervalSince1970 > createdDate.timeIntervalSince1970) && (date.timeIntervalSince1970 < endedDate.timeIntervalSince1970)) {
            return true
        }
        return false
    }

    private func dateHasEvent(_ date: Date) -> Bool {
        for event in events {
            if isEvent(event, date: date) {
                return true
            }
        }
        return false
    }

    private func isOff(date: Date) -> Bool {
        for item in Global.shared.offDates {
            guard let item = item else {
                return false
            }
            if date.compareDate(with:item) {
                return true
            }
        }
        return false
    }

}

extension CalendarViewController: FSCalendarDataSource, FSCalendarDelegate, FSCalendarDelegateAppearance{
 
    func calendar(_ calendar: FSCalendar, appearance: FSCalendarAppearance, titleDefaultColorFor date: Date) -> UIColor? {
        if isOff(date: date) {
            if dateHasEvent(date) {
                return nil
            }
            return .red
        }
        return nil
    }
    func calendar(_ calendar: FSCalendar, boundingRectWillChange bounds: CGRect, animated: Bool) {
        self.calendarHeightConstraint.constant = bounds.height
        self.view.layoutIfNeeded()
    }

    func calendar(_ calendar: FSCalendar, numberOfEventsFor date: Date) -> Int {
        for item in events {
            guard let createdDate = item.createdDate, let endedDate = item.endedDate else {
                return 0
            }
            if createdDate.compareDate(with: date) || endedDate.compareDate(with: date) || ((date.timeIntervalSince1970 > createdDate.timeIntervalSince1970) && (date.timeIntervalSince1970 < endedDate.timeIntervalSince1970)) {
                return 1
            }
        }
        return 0
    }

    func calendar(_ calendar: FSCalendar, didSelect date: Date, at monthPosition: FSCalendarMonthPosition) {
        print("did select date \(date)")
        let items = events.filter { (event) -> Bool in
            guard let createdDate = event.createdDate, let endedDate = event.endedDate else {
                return false
            }
            return createdDate.compareDate(with: date) || endedDate.compareDate(with: date) || ((date.timeIntervalSince1970 > createdDate.timeIntervalSince1970) && (date.timeIntervalSince1970 < endedDate.timeIntervalSince1970))
        }
   
        self.eventSelecteds = items
        tableView.reloadData()
    }

}

extension CalendarViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return eventSelecteds.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: HomeCell.id(), for: indexPath) as! HomeCell
        cell.fillData(event: eventSelecteds[indexPath.row])
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
        let event = eventSelecteds[indexPath.row]
        let vc: DetailHolidayViewController = DetailHolidayViewController()
        vc.event = event
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
