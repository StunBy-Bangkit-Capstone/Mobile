package com.example.stunby.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunby.databinding.FragmentHistoryBinding
import com.example.stunby.ui.ViewModelFactory
import com.example.stunby.ui.adapter.HistoryAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.example.stunby.ui.adapter.HistoryPagerAdapter

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: HistoryAdapter
    private lateinit var viewPagerAdapter: HistoryPagerAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup ViewPager2
        setupViewPager()

        // Observe history data

        return root
    }

    private fun setupViewPager() {
        viewPagerAdapter = HistoryPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        // Setup TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Measure"
                1 -> "Nutrition"
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
