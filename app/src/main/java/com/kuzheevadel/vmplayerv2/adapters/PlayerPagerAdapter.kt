package com.kuzheevadel.vmplayerv2.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class PlayerPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    private val mFragmentList: MutableList<Fragment> = mutableListOf()
    private val mTitlesList: MutableList<String> = mutableListOf()

    override fun getItem(position: Int): Fragment {
       return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitlesList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mTitlesList.add(title)
    }
}