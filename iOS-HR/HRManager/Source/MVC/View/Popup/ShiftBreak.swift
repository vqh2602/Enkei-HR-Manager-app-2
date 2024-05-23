//
//  ShiftBreaks.swift
//  HRManager
//
//  Created by Tienvv on 15/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import UIKit

protocol ShiftBreakDelegate: class {
    func shiftBreakDidSelectOk(_ shiftBreaksSelected: ShiftBreaks)
}


class ShiftBreak: PopupBaseView {
    
    @IBOutlet weak var mainView: UIView!
    @IBOutlet weak var tableView: UITableView!
    
    weak var delegate:ShiftBreakDelegate? = nil
    var categorys: [ShiftBreaks] = []
    var shiftBreaksSelected: ShiftBreaks = .allDay
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.mainView.layer.cornerRadius = 3
        self.view.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        self.showAnimate()
        // let tapGestrue: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(onTapGesture(_:)))
        //        self.view.addGestureRecognizer(tapGestrue)
        self.tableView.separatorStyle = .none
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.register(UINib(nibName: "CategoryReasonCell", bundle: nil), forCellReuseIdentifier: "CategoryReasonCell")
        
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.tabBarController?.tabBar.isHidden = false
    }
    
    
    @IBAction func didTapOk(_ sender: Any) {
        self.delegate?.shiftBreakDidSelectOk(self.shiftBreaksSelected)
        self.removeAnimate()
    }
    
    @IBAction func didTapCancel(_ sender: Any) {
        self.removeAnimate()
    }
    
    @objc func onTapGesture(_ gesture: UITapGestureRecognizer) {
        self.removeAnimate()
    }

}

extension ShiftBreak: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return categorys.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CategoryReasonCell", for: indexPath) as! CategoryReasonCell
        cell.setupCell(self.categorys[indexPath.row].description, self.shiftBreaksSelected, nil)
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 50
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.shiftBreaksSelected = self.categorys[indexPath.row]
        self.tableView.reloadData()
    }
    
    
}

