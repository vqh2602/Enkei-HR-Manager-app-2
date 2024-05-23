//
//  ContentRegisterHollidayCell.swift
//  HRManager
//
//  Created by Tienvv on 14/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

enum ShiftBreaks: Int {
    case allDay
    case morning
    case afternoon
}

extension ShiftBreaks: CustomStringConvertible {
    var description: String {
        switch self {
        case .allDay:
            return "allDay".localized
        case .morning:
            return "morning".localized
        case .afternoon:
             return "afternoon".localized
        }
    }
    
    
}

enum ContentRegisterHollidayCellStatus: Int {
    case dayOff
    case timeOff
}

enum SelectDateType: Int {
    case start
    case end
}

protocol ContentRegisterHollidayCellDelegate: class {
    func didTapCategoryReason()
    func didSelectDatePicker(_ dateType: SelectDateType,_ hollydayType: ContentRegisterHollidayCellStatus)
    func didChooseLeaveDayType(_ type: ContentRegisterHollidayCellStatus)
    func didTapSave()
    func didTapSaveAndSend()
}


class ContentRegisterHollidayCell: UITableViewCell {
    
    @IBOutlet weak var btnDayOff: UIButton!
    @IBOutlet weak var btnTimeOff: UIButton!
    @IBOutlet weak var lblTitleEndday: UILabel!
    @IBOutlet weak var lblTitleStartDay: UILabel!
    @IBOutlet weak var lblTitleContent: UILabel!
    @IBOutlet weak var lblSingleType: UILabel!
    @IBOutlet weak var btnTimeStart: UIButton!
    @IBOutlet weak var btnTimeEnd: UIButton!
    @IBOutlet weak var tvReason: UITextField!
    @IBOutlet weak var mainView: UIView!
    @IBOutlet weak var btnCategoryReason: UIButton!

    @IBOutlet weak var btnSave: UIButton!
    @IBOutlet weak var btnSaveAndSend: UIButton!


    weak var delegate: ContentRegisterHollidayCellDelegate? = nil
    var registerHolliday = RegisterHolliday()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.selectionStyle = .none
        self.btnCategoryReason.semanticContentAttribute = .forceRightToLeft
        self.btnTimeStart.semanticContentAttribute = .forceRightToLeft
        self.btnTimeEnd.semanticContentAttribute = .forceRightToLeft

        btnSave.setTitle("Save".localized, for: .normal)
        btnSaveAndSend.setTitle("SaveSend".localized, for: .normal)
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

    }
    
    func setupText() {
        self.btnDayOff.setTitle("dayOff".localized, for: .normal)
        self.btnTimeOff.setTitle("timeOff".localized, for: .normal)
        self.lblSingleType.text = "singleType".localized
        self.btnCategoryReason.setTitle((self.registerHolliday.leaveType != nil) ? self.registerHolliday.leaveType!.typeName : "select_singleType".localized, for: .normal)
        self.lblTitleStartDay.text = "startDay".localized
        self.btnTimeStart.setTitle(self.registerHolliday.startDate.toString(dateFormat: "dd/MM/yyyy"), for: .normal)
        self.lblTitleContent.text = "contentHolliday".localized
        self.tvReason.placeholder = "reasonDetail".localized
        self.tvReason.text = self.registerHolliday.comment
    }
    
    func setupData(_ registerHolliday: RegisterHolliday) {
        self.registerHolliday = registerHolliday
        self.setupText()
        switchStastus()
    }
    
    @IBAction func didTapDayOff(_ sender: Any) {
        self.delegate?.didChooseLeaveDayType(.dayOff)
        self.switchStastus()
    }
    
    @IBAction func didTapTimeOff(_ sender: Any) {
        self.delegate?.didChooseLeaveDayType(.timeOff)
        self.switchStastus()
    }
    
    @IBAction func didTapStart(_ sender: Any) {
        self.delegate?.didSelectDatePicker(.start, self.registerHolliday.leaveGroup)
    }
    
    @IBAction func didTapEnd(_ sender: Any) {
        self.delegate?.didSelectDatePicker(.end, self.registerHolliday.leaveGroup)
    }
    
    @IBAction func didTapCategoryReason(_ sender: Any) {
        self.delegate?.didTapCategoryReason()
    }

    @IBAction func didTapSave(_ sender: Any) {
        self.delegate?.didTapSave()
    }
    
    
    @IBAction func didTapSaveAndSend(_ sender: Any) {
        self.delegate?.didTapSaveAndSend()
    }

    func switchStastus() {
        if self.registerHolliday.leaveGroup == .dayOff {
            self.btnDayOff.setTitleColor(UIColor.textHighlight, for: .normal)
            self.btnTimeOff.setTitleColor(UIColor.darkText, for: .normal)
            self.btnTimeEnd.setImage(UIImage(named: "ic_calendar"), for: .normal)
            self.btnTimeEnd.setTitle(self.registerHolliday.endDate.toString(dateFormat: "dd/MM/yyyy"), for: .normal)
            self.lblTitleEndday.text = "endDay".localized
        } else {
            self.btnTimeOff.setTitleColor(UIColor.textHighlight, for: .normal)
            self.btnDayOff.setTitleColor(UIColor.darkText, for: .normal)
            self.btnTimeEnd.setImage(UIImage(named: "ic_arrow_down"), for: .normal)
            self.btnTimeEnd.setTitle(self.registerHolliday.typeOff.description, for: .normal)
            self.lblTitleEndday.text = "ca_kip".localized
        }
    }
    
}
