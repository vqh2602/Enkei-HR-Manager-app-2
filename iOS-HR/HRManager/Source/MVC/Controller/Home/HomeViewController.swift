//
//  HomeViewController.swift
//  HRManager
//
//  Created by Tinh Vu on 1/3/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

enum FilterViewStatus: Int {
    case open
    case close
}

class HomeViewController: BaseVC {

    @IBOutlet weak var lbTitle: UILabel!
    @IBOutlet weak var filterView: UIView!
    @IBOutlet weak var tabbleView: UITableView!
    @IBOutlet weak var tfSearch: UITextField!
    @IBOutlet weak var heightFilterView: NSLayoutConstraint!
    @IBOutlet weak var tableviewBottom: NSLayoutConstraint!
    @IBOutlet weak var btnAddBottom: NSLayoutConstraint!
    @IBOutlet weak var topViewTop: NSLayoutConstraint!
    var filterViewStatus: FilterViewStatus = .close
    
    lazy var collectionFilterView: FilterEventView = {[weak self] in
        let v = FilterEventView(frame: .zero)
        v.backgroundColor = UIColor.white
        v.delegate = self
        return v
    }()

    var events: [Event] = []
    var filterModel = EventFilter(userId: Global.shared.user?.userId)

    private var isFirstTime = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTappedAround()
        self.hidenNavi(true)
        self.setupView()
        self.registerCell()

        if #available(iOS 11.0, *) {
        } else {
            tableviewBottom.constant = 50
            btnAddBottom.constant = 70
            topViewTop.constant = 20
            view.updateConstraints()
        }

    }

    override func viewWillAppear(_ animated: Bool) {
        getEvents(model: filterModel)
    }
    
    func setupView() {
        //search
        self.tfSearch.layer.cornerRadius = 18
        self.tfSearch.clipsToBounds = true
        self.tfSearch.layer.borderWidth = 0.5
        self.tfSearch.layer.borderColor = UIColor.lightGray.cgColor

        lbTitle.text = "Home.Title".localized
        // filter
        self.setHeightFilterView(self.filterViewStatus)
        self.filterView.addSubview(self.collectionFilterView)
        self.filterView.addConstraints(withFormat: "H:|-8-[v0]-8-|", views: self.collectionFilterView)
        self.filterView.addConstraints(withFormat: "V:|-0-[v0]-0-|", views: self.collectionFilterView)
        
        
        //tableView
        self.tabbleView.separatorStyle = .none
        
    }
    
    
    func registerCell() {
        self.tabbleView.delegate = self
        self.tabbleView.dataSource = self
        self.tabbleView.register(UINib(nibName: "HomeCell", bundle: nil), forCellReuseIdentifier: "HomeCell")
    }

    @objc private func getEvents(model: EventFilter? = nil) {
        if isFirstTime {
            self.showLoading()
        } else {
            UIApplication.shared.isNetworkActivityIndicatorVisible = true
        }

        APIClient.shared.getEvents(model, completionHandler: { (events, error) in
            if self.isFirstTime {
                self.hidenLoading()
                self.isFirstTime = false
            } else {
                UIApplication.shared.isNetworkActivityIndicatorVisible = false
            }
            guard let response = events, let data = response.dataList else {
                self.showMessage(message: "Error")
                return
            }
            self.events = data
            self.tabbleView.reloadData()
        })
    }

    private func deleteEvent(registerId: String?) {
        guard let id = registerId else {
            return
        }
        self.showLoading()
        APIClient.shared.deleteEvent(registerId: id) { (result, error) in
            self.hidenLoading()
            if let response = result, response.successed {
                self.getEvents(model: self.filterModel)
            }
        }
    }

   
    @IBAction func didTapFiller(_ sender: Any) {
        self.filterViewStatus = self.filterViewStatus == .open ? .close : .open
        self.setHeightFilterView(self.filterViewStatus)
    }
    
    func setHeightFilterView(_ status: FilterViewStatus) {
        self.heightFilterView.constant = self.filterViewStatus == .open ? 75 : 0
    }
    
    
    @IBAction func didTapRegisterHoliday(_ sender: Any) {
        let vc: RegisterHolidayViewController = RegisterHolidayViewController()
        self.navigationController?.pushViewController(vc, animated: true)
    }

  @IBAction func searching(_ sender: Any) {
    guard let text = tfSearch.text else {
      return
    }
    print(text)
    filterModel.textSearch = text
    NSObject.cancelPreviousPerformRequests(withTarget: self, selector: #selector(self.getEvents(model:)), object: nil)
    self.perform(#selector(self.getEvents(model:)), with: filterModel, afterDelay: 1)
  }


}

extension HomeViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return events.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "HomeCell", for: indexPath) as! HomeCell
        cell.fillData(event: events[indexPath.row])
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {

        if #available(iOS 11, *) {
            return 196.0
        } else {
            return 216.0
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let event = events[indexPath.row]
        let vc: DetailHolidayViewController = DetailHolidayViewController()
        vc.event = event
        self.navigationController?.pushViewController(vc, animated: true)
    }

    func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]?
    {
        let event = events[indexPath.row]
        guard event.state.deleteable else {
            return nil
        }
        let deleteAction = UITableViewRowAction(style: .destructive, title: "Delete".localized) { (action, indexpath) in
          self.showMessageConfirm(message: "ConfirmDeleteForm".localized, handler: {
            self.deleteEvent(registerId: event.registerId)
          })
        }
        deleteAction.backgroundColor = .red
        return [deleteAction]
    }

    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool
    {
        let event = events[indexPath.row]
        return event.state.deleteable
    }
}

extension HomeViewController: FilterViewDelegate {
    func didSelectedFilter(filterView: FilterEventView, filters: [EventStatus]) {
        filterModel.flowState = filters
        self.getEvents(model: filterModel)
    }
}
