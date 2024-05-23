//
//  Picker.swift
//  HRManager
//
//  Created by Tienvv on 14/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import UIKit

protocol PickerDelegate: class {
    func done(date: Date, selectDateType: SelectDateType)
    func cancel()
}

class Picker: PopupBaseView {
    
    @IBOutlet weak var otherView: UIView!
    @IBOutlet weak var myPicker: UIDatePicker!
    @IBOutlet weak var viewToolbar: UIView!
    var selectDateType: SelectDateType = .start
    
    weak var delegate: PickerDelegate? = nil
    var displayDate: Date?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        self.otherView.alpha = 0
        self.showAnimate()
        self.setupPicker()
        myPicker.datePickerMode = .date
        myPicker.minuteInterval = 5
        myPicker.locale = Locale(identifier: "en_GB")
        myPicker.date = displayDate ?? Date()
        myPicker.maximumDate = Global.shared.maximumDate
        myPicker.minimumDate = Global.shared.minimumDate
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    func setupPicker() {
        //ToolBar
        let toolbar = UIToolbar()
        toolbar.sizeToFit()
        let doneButton = UIBarButtonItem(title: "Done", style: .plain, target: self, action: #selector(donedatePicker));
        let spaceButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        let cancelButton = UIBarButtonItem(title: "Cancel", style: .plain, target: self, action: #selector(cancelDatePicker));
        toolbar.setItems([cancelButton, spaceButton, doneButton], animated: false)
        self.viewToolbar.addSubview(toolbar)
        toolbar.isHidden = false
        toolbar.isUserInteractionEnabled = true
    }
    
    @objc func donedatePicker(){
        self.removeAnimate()
        self.view.endEditing(true)
        self.delegate?.done(date: myPicker.date, selectDateType: self.selectDateType)
    }
    
    @objc func cancelDatePicker(){
        self.removeAnimate()
        self.view.endEditing(true)
        self.delegate?.cancel()
    }
    
    @IBAction func didTapOtherView(_ sender: Any) {
        self.removeAnimate()
        self.view.endEditing(true)
    }
}
