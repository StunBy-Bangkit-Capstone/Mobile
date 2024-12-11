package com.example.stunby.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.stunby.R
import com.example.stunby.ui.history.HistoryMeasureFragment
import com.example.stunby.ui.history.HistoryNutritionFragment

class HistoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // Two tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoryMeasureFragment()
            1 -> HistoryNutritionFragment()
            else -> HistoryMeasureFragment()
        }
    }


    class NutritionFragment : Fragment(R.layout.fragment_history_nutrition) {
        // Handle UI and logic related to the "Nutrition" tab
    }
}
