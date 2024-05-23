//
//  CategotyReson.swift
//  HRManager
//
//  Created by Tienvv on 14/01/2019.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import Foundation
import UIKit

protocol CategoryReasonDelegate: class {
    func categoryReasonDidSelectOk(_ categorySelected: LeaveType)
}

class CategoryReason: PopupBaseView {
    
    @IBOutlet weak var mainView: UIView!
    @IBOutlet weak var tableView: UITableView!
    weak var delegate:CategoryReasonDelegate? = nil
    var categorys: [LeaveType] = []
    var categorySelected: LeaveType?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.mainView.layer.cornerRadius = 3
        self.view.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        self.showAnimate()
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
        if let selected = self.categorySelected {
            self.delegate?.categoryReasonDidSelectOk(selected)
        }
        self.removeAnimate()
    }
    
    @IBAction func didTapCancel(_ sender: Any) {
        self.removeAnimate()
    }
    
    @objc func onTapGesture(_ gesture: UITapGestureRecognizer) {
        self.removeAnimate()
    }

}

extension CategoryReason: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return categorys.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CategoryReasonCell", for: indexPath) as! CategoryReasonCell
        cell.setupCell(self.categorys[indexPath.row].typeName, nil, self.categorySelected)
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 50
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.categorySelected = self.categorys[indexPath.row]
        self.tableView.reloadData()
    }

    
}
