package com.athsoftware.hrm.views

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by tinhvv on 11/13/18.
 */

class CommonPagerAdapter(fm: FragmentManager?,
                         private val fragments: List<Fragment?>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}