//
//  NotificationTableViewCell.swift
//  HRManager
//
//  Created by Tinh Vu on 1/14/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class NotificationTableViewCell: UITableViewCell {
    @IBOutlet weak var lbTime: UILabel!
    @IBOutlet weak var lbContent: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

  func fillData(notifi: Notification) {
    lbContent.text = notifi.title
    lbTime.text = notifi.ago
  }
    
}
