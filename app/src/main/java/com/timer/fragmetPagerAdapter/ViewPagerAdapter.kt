package com.timer.fragmetPagerAdapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        when(position){
            0 ->{
               val chat =ChatFrgment()
                return chat
            }
            1 ->{
                val profile=ProfileFragment()
                return profile
            }
        }
     return null
    }

    override fun getCount(): Int {
        return 2
    }
}