//
//  ActionRegisterHollidayCell.swift
//  HRManager
//
//  Created by Tienvv on 14/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

protocol ActionRegisterHollidayCellDelegate: class {
    func didTapSave(workFlow: String?)
    func didTapSaveAndSend(workFlow: String?)
    func didTapSingle(workFlow: String)
}


class ActionRegisterHollidayCell: UITableViewCell {
    weak var delegate: ActionRegisterHollidayCellDelegate? = nil
    @IBOutlet weak var singleView: UIView!
    @IBOutlet weak var viewBtn1: UIView!
    @IBOutlet weak var viewBtn2: UIView!
    @IBOutlet weak var btn1: UIButton!
    @IBOutlet weak var btn2: UIButton!
    @IBOutlet weak var btnSingle: UIButton!
    var flow: [String] = []
    var type: Int = 1 // 0 is detail, 1 is register
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.selectionStyle = .none
        if type == 1 {
            singleView.isHidden = true
          btn1.setTitle("Save".localized, for: .normal)
          btn2.setTitle("SaveSend".localized, for: .normal)
        }
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    func setupButton(flow: [String]) {
        self.flow = flow
        self.type = 0
        if self.type == 0 {
            if flow.count == 0 {
                singleView.isHidden = true
                viewBtn1.isHidden = true
                viewBtn2.isHidden = true
            } else if flow.count == 1 {
                viewBtn1.isHidden = true
                viewBtn2.isHidden = true
                if let flowProcess1 = WorkFlow(rawValue: flow[0]) {
                    btnSingle.setTitle(flowProcess1.description, for: .normal)
                }
            } else {
                singleView.isHidden = true
                if let flowProcess1 = WorkFlow(rawValue: flow[0]), let flowProcess2 = WorkFlow(rawValue: flow[1]) {
                    btn1.setTitle(flowProcess1.description, for: .normal)
                    btn2.setTitle(flowProcess2.description, for: .normal)
                }
            }
        }

    }
    @IBAction func didTapSingle(_ sender: Any) {
        self.delegate?.didTapSingle(workFlow: flow[0])
    }

    @IBAction func didTapSave(_ sender: Any) {
        self.delegate?.didTapSave(workFlow: flow.count > 0 ? flow[0] : nil)
    }
    
    @IBAction func didTapSaveAndSend(_ sender: Any) {
        self.delegate?.didTapSaveAndSend(workFlow: flow.count > 1 ? flow[1] : nil)
    }
}
