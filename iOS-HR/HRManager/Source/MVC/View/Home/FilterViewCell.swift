//
//  FilterViewCell.swift
//  HRManager
//
//  Created by Tienvv on 14/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class FilterViewCell: UICollectionViewCell {

    @IBOutlet weak var lblTitle: UILabel!
    @IBOutlet weak var viewContent: UIView!
    @IBOutlet weak var imvBGStatus: UIImageView!

    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    func setupCell(_ filter: EventStatus) {
        self.lblTitle.text = filter.description
        self.lblTitle.textColor = UIColor(hexString: filter.color)
    }

}

