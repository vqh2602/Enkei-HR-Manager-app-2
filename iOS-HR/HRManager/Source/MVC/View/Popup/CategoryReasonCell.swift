//
//  CategoryReasonCell.swift
//  HRManager
//
//  Created by Tienvv on 14/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit


class CategoryReasonCell: UITableViewCell {
    @IBOutlet weak var lblTitle: UILabel!
    @IBOutlet weak var imgStatus: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.selectionStyle = .none
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setupCell(_ title: String?,_ shiftBreaksSelected: ShiftBreaks?, _ categorySelected: LeaveType?) {
        self.lblTitle.text = title
        if let _shiftBreaksSelected = shiftBreaksSelected {
            self.imgStatus.image = UIImage(named: title == _shiftBreaksSelected.description ? "ic_single_selected" : "ic_single")
        } else {
            self.imgStatus.image = UIImage(named: title == categorySelected?.typeName ? "ic_single_selected" : "ic_single")
        }
        
    }
    
}
