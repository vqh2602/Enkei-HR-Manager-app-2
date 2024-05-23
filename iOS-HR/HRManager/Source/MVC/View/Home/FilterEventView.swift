//
//  FilterEventView.swift
//  HRManager
//
//  Created by Tinh Vu on 1/14/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

protocol FilterViewDelegate: class {
    func didSelectedFilter(filterView: FilterEventView, filters: [EventStatus])
}

class FilterEventView: UIView {
    weak var delegate: FilterViewDelegate?
    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet var view: UIView!
    let filters = [EventStatus.Draft, EventStatus.Accepted, EventStatus.Rejected, EventStatus.Done]
    var filterSelecteds: [EventStatus] = []
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
    }

    func commonInit() {
        Bundle.main.loadNibNamed(FilterEventView.id(), owner: self, options: nil)
        addSubview(view)
        view.frame = self.bounds
        view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        collectionView.register(UINib(nibName: FilterViewCell.id(), bundle: nil), forCellWithReuseIdentifier: FilterViewCell.id())

    }
}

extension FilterEventView: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return filters.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "FilterViewCell", for: indexPath) as! FilterViewCell
        let filter = filters[indexPath.row]
        cell.setupCell(filter)
        if filterSelecteds.contains(filter) {
            cell.imvBGStatus.image = cell.imvBGStatus.image!.withRenderingMode(.alwaysTemplate)
            var tintColor = "#9ea0a5"
            switch (filter) {
            case .Draft: tintColor = "#9ea0a5"
            case .Accepted: tintColor = "#2c66ef"
            case .Done: tintColor = "#4ece3d"
            default:
                tintColor = "#ff151f"
            }
            cell.imvBGStatus.tintColor = UIColor(hexString: tintColor)
            cell.imvBGStatus.alpha = 0.3
        } else {
            cell.imvBGStatus.image = UIImage(named: "bg_status")
        }
        return cell
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: 95, height: 32)
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let filter = filters[indexPath.row]
        if let index = filterSelecteds.firstIndex(of: filter) {
            filterSelecteds.remove(at: index)
        } else {
            filterSelecteds.append(filter)
        }
        self.delegate?.didSelectedFilter(filterView: self, filters: filterSelecteds)
        collectionView.reloadData()
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 2
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 2
    }
}
