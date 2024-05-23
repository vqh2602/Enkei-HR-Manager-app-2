//
//  HomeCell.swift
//  HRManager
//
//  Created by Tienvv on 13/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class HomeCell: UITableViewCell {
    
    @IBOutlet weak var mainView: UIView!
    @IBOutlet weak var avatar: UIImageView!
    @IBOutlet weak var lblName: UILabel!
    @IBOutlet weak var lblSendDate: UILabel!
    @IBOutlet weak var lblCategoryReason: UILabel!
    @IBOutlet weak var lblNumberOfHolidays: UILabel!
    @IBOutlet weak var lblStatus: UILabel!
    @IBOutlet weak var lblDetailReason: UILabel!
    @IBOutlet weak var lbRangeDate: UILabel!
    @IBOutlet weak var imvBGStatus: UIImageView!
    @IBOutlet weak var lbComment: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        avatar.image = nil
    }

    private func getAvatar(userId: String?) {
        guard let `userId` = userId else { return }
        if let imageString = UserDefaults.standard.object(forKey: userId) as? String {
            setupImage(imageString: imageString)
            return
        }
        APIClient.shared.getProfile(userId) { (result, error) in
            guard let response = result else {
                return
            }
            let user = response.data
            if let img = user?.staffImg {
                self.setupImage(imageString: img)
                UserDefaults.standard.set(img, forKey: userId)
            }
        }
    }

    private func setupImage(imageString: String?) {
        if let imageString = imageString, let imageData = Data(base64Encoded: imageString) {

            DispatchQueue.global().async {
                let image = UIImage(data: imageData)?.fixedOrientation()
                DispatchQueue.main.async {
                    self.avatar.image = image
                }
            }
        }
    }

    func fillData(event: Event) {
        guard let user = event.createdByUser else {
            return
        }
        getAvatar(userId: user.userId)
        lblName.text = event.createdBy
        if let modifiedDate = event.lastModifiedOnDate {
            lblSendDate.text = modifiedDate.toString()
        }
        lblCategoryReason.text = "Home.LeaveType".localized + (event.leaveTypeName ?? "")
        if let days = event.countDay {
            lblNumberOfHolidays.text = "Home.NumberDay".localized + "\(days) \(days > 1 ? "Home.Days".localized : "Home.Day".localized)"
            
        }
        if let statusString = event.workflowState, let status = WorkFlow(rawValue: statusString) {
            lblStatus.text = status.description
            lblStatus.textColor = status.color.toColor()
            imvBGStatus.image = imvBGStatus.image!.withRenderingMode(.alwaysTemplate)
            var tintColor = "#9ea0a5"
            switch (status) {
            case .Draft: tintColor = "#9ea0a5"
            case .HRExecute, .QLC1Execute, .QLC2Execute: tintColor = "#2c66ef"
            case .StoreContent: tintColor = "#4ece3d"
            default:
                tintColor = "#ff151f"
            }
            imvBGStatus.tintColor = UIColor(hexString: tintColor)
            imvBGStatus.alpha = 0.3
        }
        lblDetailReason.text = event.des
        lbComment.text = event.comment
        if let startDate = event.startDate, let endDate = event.endDate {
            lbRangeDate.text = "\(startDate.toString()) - \(endDate.toString())"
        }
    }
    
}
