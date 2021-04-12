package com.evitalz.homevitalz.heartskan.ui.activities.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.evitalz.homevitalz.heartskan.ui.fragments.analytics.AnalyticsFragment
import com.evitalz.homevitalz.heartskan.ui.fragments.home.HomeFragment

class HomePagerAdapter(fragmentManager: FragmentActivity) : FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
        return 2
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun createFragment(position: Int): Fragment {
        return if(position == 0){
            HomeFragment()
        }else{
            AnalyticsFragment()
        }
    }

}