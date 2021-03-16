package com.evitalz.homevitalz.cardfit.ui.fragments.analytics

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.evitalz.homevitalz.cardfit.ui.fragments.analytics.graphviewer.BloodGlucoseFragment
import com.evitalz.homevitalz.cardfit.ui.fragments.analytics.graphviewer.EcgFragment

class MyPagerAdapter(fragmentManager: Fragment) : FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0){
            BloodGlucoseFragment()
        }else{
            EcgFragment()
        }
    }

}