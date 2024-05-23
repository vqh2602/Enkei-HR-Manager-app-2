//
//  CreatedSuccessViewController.swift
//  HRManager
//
//  Created by Tinh Vu on 1/3/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

class CreatedSuccessViewController: BaseVC {

    @IBOutlet weak var lbTitle: UILabel!
    @IBOutlet weak var lbDetail: UILabel!
    @IBOutlet weak var btnHome: UIButton!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hidenNavi(true)
        lbTitle.text = "Result.Title".localized
        lbDetail.text = "Result.Detail".localized
        btnHome.setTitle("Result.Home".localized, for: .normal)
    }


    @IBAction func didTapHome(_ sender: Any) {
        self.navigationController?.popToRootViewController(animated: true)
    }


}
