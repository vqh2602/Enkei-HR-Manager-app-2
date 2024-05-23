//
//  AvatarTableViewCell.swift
//  HRManager
//
//  Created by Tinh Vu on 1/17/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class AvatarTableViewCell: UITableViewCell {
    @IBOutlet weak var imvAvatar: UIImageView!
    @IBOutlet weak var lbName: UILabel!
    @IBOutlet weak var lbCreatedDate: UILabel!
    @IBOutlet weak var lbStatus: UILabel!
    @IBOutlet weak var imvBGStatus: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

  private func setupImage(imageString: String?) {
    if let imageString = imageString, let imageData = Data(base64Encoded: imageString) {

      DispatchQueue.global().async {
        let image = UIImage(data: imageData)?.fixedOrientation()
        DispatchQueue.main.async {
          self.imvAvatar.image = image
        }
      }
    }
  }

    func fillData(event: Event?) {
        guard let `event` = event, let userId = event.createdByUser?.userId else {
            return
        }
      if let imageString = UserDefaults.standard.object(forKey: userId) as? String {
        setupImage(imageString: imageString)
      }
        lbName.text = event.createdBy
        if let createdDate = event.createdOnDate {
            lbCreatedDate.text = createdDate.toString()
        }
        if let statusString = event.workflowState, let status = WorkFlow(rawValue: statusString) {
            lbStatus.text = status.description
            lbStatus.textColor = status.color.toColor()

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
    }
    
}
