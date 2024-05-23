package com.athsoftware.hrm.views.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.athsoftware.hrm.App
import com.athsoftware.hrm.R
import com.athsoftware.hrm.base.BaseFragment
import com.athsoftware.hrm.base.IBasePresenter
import com.athsoftware.hrm.base.IBaseView
import com.athsoftware.hrm.common.Constants
import com.athsoftware.hrm.helper.extensions.PreferencesHelper
import com.athsoftware.hrm.helper.extensions.loadBase64
import com.athsoftware.hrm.helper.extensions.setImageForName
import com.athsoftware.hrm.helper.extensions.toOffDay
import com.athsoftware.hrm.model.User
import com.athsoftware.hrm.views.authen.LoginActivity
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment(), ProfileContractor.View {
    override fun showProfile(user: User) {
        tvName.text = user.fullName
        tvPhone.text = user.mobile
        tvStaffCode.text = user.staffCode
        tvPosition.text = user.position
        ivAvatar.loadBase64(user.staffImg ?: return)
    }

    override fun showDayOff(dayOff: Float) {
        tvRemainDayOff.text = dayOff.toOffDay()
    }

    private val presenter = ProfilePresenter()

    override fun getPresenter(): IBasePresenter<IBaseView>? {
        return presenter as IBasePresenter<IBaseView>
    }

    override fun getLayoutRes(): Int = R.layout.fragment_profile
    override val isTransfStatus = false
    override fun isShowToolbar(): Boolean {
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvName.text = App.shared().auth?.fullName
        tvPhone.text = App.shared().auth?.phoneNumber
        tvStaffCode.text = ""
        showAvatar()
        presenter.getProfile()
        presenter.getDayOff()
        btnLogout.setOnClickListener {
            App.shared().auth = null
            PreferencesHelper.shared.clear()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            activity?.startActivity(intent)
            activity?.finish()
            App.unregisterDeviceToken()
        }
    }

    private fun showAvatar() {
        val userImage = App.shared().imageCache[App.userId]
        if (userImage != null) {
            if (userImage.isNotEmpty()) {
                ivAvatar.loadBase64(userImage)
            } else {
                ivAvatar.setImageForName(App.shared().auth?.fullName ?: "")
            }
        } else {
            ivAvatar.setImageForName(App.shared().auth?.fullName ?: "")
        }
    }
}