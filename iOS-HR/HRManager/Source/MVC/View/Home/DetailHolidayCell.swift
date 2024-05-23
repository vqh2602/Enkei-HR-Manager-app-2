//
//  DetailHolidayCell.swift
//  HRManager
//
//  Created by Tienvv on 15/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class DetailHolidayCell: UITableViewCell {

    ///fix text---------------------------------
    @IBOutlet weak var lblTitlePosition: UILabel!
    @IBOutlet weak var lblTitleEmployeeNumber: UILabel!
    @IBOutlet weak var lblTitlePhoneNumber: UILabel!
    @IBOutlet weak var lblTitleCtegory: UILabel!
    @IBOutlet weak var lblTitleDateHoliday: UILabel!
    @IBOutlet weak var lblTitleHolidayNumber: UILabel!
    @IBOutlet weak var lblTitleApprover: UILabel!
    @IBOutlet weak var lblTitleContent: UILabel!
    //------------------------------------------
    
    //text change-------------------------------
    @IBOutlet weak var lblPosition: UILabel!
    @IBOutlet weak var lblEmployeeNumber: UILabel!
    @IBOutlet weak var lblPhoneNumber: UILabel!
    @IBOutlet weak var lblCtegory: UILabel!
    @IBOutlet weak var lblDateHoliday: UILabel!
    @IBOutlet weak var lblHolidayNumber: UILabel!
    @IBOutlet weak var lblApprover: UILabel!
    @IBOutlet weak var lblContent: UILabel!
    @IBOutlet weak var lblName: UILabel!
    @IBOutlet weak var lblDateSend: UILabel!
    @IBOutlet weak var lblStatus: UILabel!
    @IBOutlet weak var imvAvatar: UIImageView!

    //------------------------------------------
    
    override func awakeFromNib() {
        super.awakeFromNib()
        setupTitle()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    private func setupTitle() {
        lblTitlePosition.text = "Detail.Position".localized
        lblTitleEmployeeNumber.text = "Detail.StaffCode".localized
        lblTitlePhoneNumber.text = "Detail.Phone".localized
        lblTitleCtegory.text = "Detail.LeaveType".localized
        lblTitleDateHoliday.text = "Detail.OffDays".localized
        lblTitleHolidayNumber.text = "Detail.CountDay".localized
        lblTitleApprover.text = "Detail.Accepter".localized
        lblTitleContent.text = "Detail.Content".localized
    }

    func fillData(event: Event?) {
        guard let `event` = event else {
            return
        }
        imvAvatar.image = Global.shared.fakeAvatar(userId: event.createdByUser?.userId)
        lblName.text = event.createdBy
        if let createdDate = event.createdOnDate {
            lblDateSend.text = createdDate.toString()
        }
        if let statusString = event.workflowState, let status = EventStatus(rawValue: statusString) {
            lblStatus.text = status.description
            lblStatus.textColor = status.color.toColor()
        }
        lblPosition.text = event.createdByUser?.position
        lblEmployeeNumber.text = event.createdByUser?.staffCode
        lblPhoneNumber.text = event.createdByUser?.mobile
        lblCtegory.text = event.leaveTypeName
        if let startDate = event.startDate, let endDate = event.endDate {
            lblDateHoliday.text = "\(startDate.toString()) - \(endDate.toString())"
        }
        lblHolidayNumber.text = "\(event.countDay ?? 0)"
        lblApprover.text = event.transitionUser?.fullName
        lblContent.text = event.des
        
    }
    
}
