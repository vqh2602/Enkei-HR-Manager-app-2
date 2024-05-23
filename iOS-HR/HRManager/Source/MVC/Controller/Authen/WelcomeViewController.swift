//
//  WelcomeViewController.swift
//  HRManager
//
//  Created by Tinh Vu on 1/3/19.
//  Copyright © 2019 Tinh Vu. All rights reserved.
//

import UIKit

extension UIColor {
    convenience init(hexString:String) {
        let scanner = Scanner(string: hexString.trimmed)

        if (hexString.hasPrefix("#")) {
            scanner.scanLocation = 1
        }

        var color:UInt32 = 0
        scanner.scanHexInt32(&color)

        let mask = 0x000000FF
        let r = Int(color >> 16) & mask
        let g = Int(color >> 8) & mask
        let b = Int(color) & mask

        let red   = CGFloat(r) / 255.0
        let green = CGFloat(g) / 255.0
        let blue  = CGFloat(b) / 255.0

        self.init(red:red, green:green, blue:blue, alpha:1)
    }
}

class WelcomeViewController: BaseVC {
    @IBOutlet weak var collectionView: UICollectionView!
    var items = ["Leave Management", "Employee Satisfaction Survey", "Employee Evaluation", "Employee Registration"]
    var images = ["ic_wc1", "ic_wc2", "ic_wc3", "ic_wc4"]
    var textColor = ["#2e80f9", "#f9ac2e", "#ce37fa", "#21d5f2"]
    override func viewDidLoad() {
        super.viewDidLoad()
        collectionView.register(UINib(nibName: WelcomeCollectionViewCell.id(), bundle: nil), forCellWithReuseIdentifier: WelcomeCollectionViewCell.id())
    }

}

extension WelcomeViewController: UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 4
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: WelcomeCollectionViewCell.id(), for: indexPath) as! WelcomeCollectionViewCell
        cell.imv.image = UIImage(named: images[indexPath.row])
        cell.lbTitle.text = items[indexPath.row]
        cell.lbTitle.textColor = UIColor(hexString: textColor[indexPath.row])
        return cell
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        var height: CGFloat = 200
        if #available(iOS 11, *) {

        } else {
            height = 220
        }
        return CGSize(width: (collectionView.width - 10) / 2, height: height)
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if indexPath.row == 0 {
            AppDelegate.shared.switchRootVC()
        } else {
            self.showMessage(message: "Coming soon")
        }
    }
}
