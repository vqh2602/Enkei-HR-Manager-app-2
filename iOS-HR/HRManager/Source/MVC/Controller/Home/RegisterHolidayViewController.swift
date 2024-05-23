//
//  RegisterHolidayViewController.swift
//  HRManager
//
//  Created by Tienvv on 14/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

enum RegisterHolidayViewControllerCellType: Int {
    case numberOfDaysRemaining
    case contentRegisterHolliday
    case actionRegisterHolliday
}

class RegisterHolidayViewController: BaseVC {
    let datePicker = UIDatePicker()
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var lbTitle: UILabel!
    @IBOutlet weak var topViewTop: NSLayoutConstraint!
    @IBOutlet weak var topViewHeight: NSLayoutConstraint!
    @IBOutlet weak var lbNumber: UILabel!
    @IBOutlet weak var lbDescription: UILabel!
    @IBOutlet weak var calendar: FSCalendar!
    @IBOutlet weak var calendarHeightConstrains: NSLayoutConstraint!
    
    let registerHolliday: RegisterHolliday = RegisterHolliday()
    var contentCell: ContentRegisterHollidayCell!
    var leaveTypes: [LeaveType] = []

    var selectDateType: SelectDateType = .start
    var selectedDate: Date = Date()

    var dateSelectedCalendar: Date = Date()
    var events: [Event]?
    var filterModel = EventFilter(userId: Global.shared.user?.userId)

    fileprivate let gregorian = Calendar(identifier: .gregorian)
    fileprivate let formatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter
    }()

    lazy var calendarView: WSCalendarView = {
        let cv = Bundle.main.loadNibNamed("WSCalendarView", owner: self, options: nil)?.first as! WSCalendarView
        cv.tappedDayBackgroundColor = .blue
        cv.calendarStyle = 0
        cv.isShowEvent = false
        cv.setupAppearance()
        cv.delegate = self
        return cv
    }()

    var event: Event? {
        didSet {
            registerHolliday.leaveGroup = ContentRegisterHollidayCellStatus(rawValue: event!.leaveGroup) ?? .dayOff
            registerHolliday.leaveType = LeaveType(id: "\(event?.leaveTypeId ?? 0)", name: event?.leaveTypeName)
            registerHolliday.comment = event?.des ?? ""
            registerHolliday.startDate = (event?.startDate ?? "").toDate() ?? Date()
            registerHolliday.endDate = (event?.endDate ?? "").toDate() ?? Date()
            registerHolliday.typeOff = ShiftBreaks(rawValue: event!.thoiGianNghi) ?? .allDay
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupTable()
        getLeaveTypes()
        getRemainDay()
        self.view.addSubview(calendarView)
//        setupCalendar()
        if #available(iOS 11.0, *) {
        } else {
            topViewTop.constant = 20
            topViewHeight.constant = 90
        }

        getEvents()
    }
    
    func setupTable() {
        self.tableView.separatorStyle = .none
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.register(UINib(nibName: "MumberOfDaysRemainingCell", bundle: nil), forCellReuseIdentifier: "MumberOfDaysRemainingCell")
        self.tableView.register(UINib(nibName: "ContentRegisterHollidayCell", bundle: nil), forCellReuseIdentifier: "ContentRegisterHollidayCell")
        self.tableView.register(UINib(nibName: "ActionRegisterHollidayCell", bundle: nil), forCellReuseIdentifier: "ActionRegisterHollidayCell")

        lbTitle.text = "LeaveForm.Title".localized
        lbDescription.text = "Detail.RemainDay".localized
    }

    private func setupCalendar() {
        self.calendar.select(dateSelectedCalendar)
        calendar.register(DIYCalendarCell.self, forCellReuseIdentifier: "cell")
    }

    private func getLeaveTypes() {
        APIClient.shared.getLeaveType { (result, error) in
            if let types = result?.dataList {
                self.leaveTypes = types
            }
        }
    }

    private func getRemainDay() {
        APIClient.shared.getOffTime { (remainDay, error) in
            if let `remainDay` = remainDay {
                self.registerHolliday.remainDay = remainDay
                self.lbNumber.text = "\(remainDay)"
            }
        }
    }

    private func register(toNext: Bool) {
        self.showLoading()
        APIClient.shared.registerEvent(model: registerHolliday) { (result, error) in
            self.hidenLoading()
            guard let response = result else {
                self.showMessage(message: "HaveError".localized)
                return
            }
            if response.successed {
                if toNext {
                    let id = response.data?.registerId
                    self.requestAccept(id: id)
                } else {
                    self.gotoSuccess()
                }
            } else {
                self.showMessage(message: response.message)
            }
        }
    }

    private func edit(model: EditHoliday, toNext: Bool) {
        self.showLoading()
        APIClient.shared.updateEvent(model: model) { (result, error) in
            self.hidenLoading()
            guard let response = result else {
                self.showMessage(message: "HaveError".localized)
                return
            }
            if response.successed {
                if toNext {
                    let id = response.data?.registerId
                    self.requestAccept(id: id)
                } else {
                    self.gotoSuccess()
                }
            } else {
                self.showMessage(message: response.message)
            }
        }
    }

    private func requestAccept(id: String?) {
        guard let registerId = id else { return }
        self.showLoading()
        APIClient.shared.process(registerId: registerId, comment: "", command: Processing.StartProcessing.rawValue) { (result, error) in
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

    private func getEvents() {
        filterModel.fromDate = Date().startOfMonth()
        filterModel.toDate = Date().endOfMonth()
        self.showLoading()
        APIClient.shared.getEvents(filterModel, completionHandler: { (events, error) in
            self.hidenLoading()
            guard let response = events, let data = response.dataList else {
                self.showMessage(message: "Error")
                return
            }
            self.events = data.filter({ (event) -> Bool in
                return !event.state.isDenial && event.registerUserId == self.filterModel.createdByUserId
            })
            self.calendar.reloadData()
        })
    }

    private func gotoSuccess() {
        let vc = CreatedSuccessViewController()
        self.navigationController?.pushViewController(vc, animated: true)
    }
   
    @IBAction func didTapBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    func showPickerView(_ selectDateType: SelectDateType) {
        self.selectDateType = selectDateType
        self.calendarView.activeCalendar(view)
    }
    
    func showPopupCategory() {
        self.view.endEditing(true)
        let categoryReason: CategoryReason = CategoryReason()
        categoryReason.delegate = self
        categoryReason.categorys = leaveTypes
        categoryReason.categorySelected = registerHolliday.leaveType
        self.addChild(categoryReason)
        categoryReason.view.frame = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
        categoryReason.modalPresentationStyle = .overFullScreen
        self.view.addSubview(categoryReason.view)
        self.tabBarController?.tabBar.isHidden = true
        categoryReason.didMove(toParent: self)
    }
    
    
    func showPopupShiftBreak(_ categorys: [ShiftBreaks]) {
        self.view.endEditing(true)
        let shiftBreak: ShiftBreak = ShiftBreak()
        shiftBreak.delegate = self
        shiftBreak.categorys = categorys
        shiftBreak.shiftBreaksSelected = self.registerHolliday.typeOff
        self.addChild(shiftBreak)
        shiftBreak.view.frame = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
        shiftBreak.modalPresentationStyle = .overFullScreen
        self.view.addSubview(shiftBreak.view)
        self.tabBarController?.tabBar.isHidden = true
        shiftBreak.didMove(toParent: self)
    }
    
    
}

extension RegisterHolidayViewController : WSCalendarViewDelegate {
    func didTap(_ lblView: WSLabel!, with selectedDate: Date!) {

    }

    func deactiveWSCalendar(with selectedDate: Date!) {
        if selectDateType == .start { // start
            self.registerHolliday.startDate = selectedDate
        } else {  // end
            self.registerHolliday.endDate = selectedDate
//            self.calendar.select(selectedDate, scrollToDate: false)
        }
        self.tableView.reloadRows(at: [IndexPath.init(row: 0, section: 0)], with: .none)
        self.selectedDate = selectedDate
    }

    func setupEventForDate() -> [Any]! {
        return []
    }


}

extension RegisterHolidayViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ContentRegisterHollidayCell", for: indexPath) as! ContentRegisterHollidayCell
        self.contentCell = cell
        cell.delegate = self
        cell.setupData(self.registerHolliday)
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 390
    }
}

extension RegisterHolidayViewController: ContentRegisterHollidayCellDelegate {
    func didSelectDatePicker(_ dateType: SelectDateType, _ hollydayType: ContentRegisterHollidayCellStatus) {
        if hollydayType == .dayOff {
            self.showPickerView(dateType)
        } else { // .timeOff
            if dateType == .start {
                self.showPickerView(dateType)
            } else {
                let categorys: [ShiftBreaks] = [.allDay, .morning, .afternoon]
                self.showPopupShiftBreak(categorys)
            }
        }
    }
    
    func didTapCategoryReason() {
        self.showPopupCategory()
    }

    func didChooseLeaveDayType(_ type: ContentRegisterHollidayCellStatus) {
        self.registerHolliday.leaveGroup = type
    }

    func commentTyping(text: String) {
        self.registerHolliday.comment = text
    }

    func didTapSave() {
        registerHolliday.comment = self.contentCell.tvReason.text ?? ""
        if validate() {
            if let `event` = self.event, let id = event.registerId { // Edit
                let model = EditHoliday(register: registerHolliday, id: id)
                self.edit(model: model, toNext: false)
            } else { // Created new
                self.register(toNext: false)
            }
        }
    }

    func didTapSaveAndSend() {
        registerHolliday.comment = self.contentCell.tvReason.text ?? ""
        if validate() {
            if let `event` = self.event, let id = event.registerId { // Edit
                let model = EditHoliday(register: registerHolliday, id: id)
                self.edit(model: model, toNext: true)
            } else { // Created new
                self.register(toNext: true)
            }
        }
    }

    func validate() -> Bool {
        if registerHolliday.leaveType == nil {
            self.showMessage(message: "MustChooseLeaveType".localized)
            return false
        }

        if registerHolliday.leaveGroup == .dayOff, Calendar.current.compare(registerHolliday.startDate, to: registerHolliday.endDate, toGranularity: .day) == .orderedDescending {
            self.showMessage(message: "StartDateEndDate".localized)
            return false
        }

        if let type = registerHolliday.leaveType?.leaveOfAbsenceTypeId, let typeInt = Int(type) {
            if typeInt != 3, typeInt != 5, registerHolliday.startDate.toOriginal()!.timeIntervalSince1970 - (Date().toOriginal())!.timeIntervalSince1970 < 86400*3 {
                self.showMessage(message: "CreatedFormEarly3Day".localized)
                return false
            }
        }
        return true
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

    private func dateHasEvent(_ date: Date) -> Bool {
        guard let events = events else {
            return false
        }
        for event in events {
            if isEvent(event, date: date) {
                return true
            }
        }
        return false
    }
}

extension RegisterHolidayViewController: CategoryReasonDelegate {
    
    func categoryReasonDidSelectOk(_ categorySelected: LeaveType) {
        self.registerHolliday.leaveType = categorySelected
        self.tableView.reloadRows(at: [IndexPath.init(row: 0, section: 0)], with: .none)
    }
}

extension RegisterHolidayViewController: ShiftBreakDelegate {
    func shiftBreakDidSelectOk(_ shiftBreaksSelected: ShiftBreaks) {
        self.registerHolliday.typeOff = shiftBreaksSelected
        self.tableView.reloadRows(at: [IndexPath.init(row: 0, section: 0)], with: .none)
    }
}

extension RegisterHolidayViewController: PickerDelegate {
    func done(date: Date, selectDateType: SelectDateType) {
        if selectDateType == .start { // start
            self.registerHolliday.startDate = date
        } else {  // end
            self.registerHolliday.endDate = date
        }
        self.tableView.reloadRows(at: [IndexPath.init(row: 0, section: 0)], with: .none)
    }
    
    func cancel() {
        
    }
}

extension RegisterHolidayViewController: FSCalendarDataSource, FSCalendarDelegate, FSCalendarDelegateAppearance {
    func calendar(_ calendar: FSCalendar, appearance: FSCalendarAppearance, titleDefaultColorFor date: Date) -> UIColor? {
        if isOff(date: date) {
            if dateHasEvent(date) {
                return nil
            }
            return .red
        }
        return nil
    }

    func calendar(_ calendar: FSCalendar, numberOfEventsFor date: Date) -> Int {
        guard let events = events else {
            return 0
        }
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
        guard let events = events else {
            return
        }
        let items = events.filter { (event) -> Bool in
            guard let createdDate = event.createdDate, let endedDate = event.endedDate else {
                return false
            }
            return createdDate.compareDate(with: date) || endedDate.compareDate(with: date) || ((date.timeIntervalSince1970 > createdDate.timeIntervalSince1970) && (date.timeIntervalSince1970 < endedDate.timeIntervalSince1970))
        }
        if items.isEmpty {
            return
        }
        let vc = ListEventViewController()
        vc.events = items
        vc.date = date
        self.navigationController?.pushViewController(vc, animated: true)
    }

}
