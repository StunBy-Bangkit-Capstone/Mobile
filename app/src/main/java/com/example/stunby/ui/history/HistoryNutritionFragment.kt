package com.example.stunby.ui.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunby.R
import com.example.stunby.databinding.FragmentHistoryMeasureBinding
import com.example.stunby.databinding.FragmentHistoryNutritionBinding
import com.example.stunby.ui.ViewModelFactory
import com.example.stunby.ui.adapter.HistoryAdapter
import com.example.stunby.ui.adapter.NutritionAdapter
import com.example.stunby.ui.nutrition.NutritionViewModel


class HistoryNutritionFragment : Fragment() {

    private var _binding: FragmentHistoryNutritionBinding? = null
    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: NutritionAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryNutritionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupRecyclerView()
        observeHistory()

        return root
    }

    private fun observeHistory() {
        viewModel.nutrition.observe(viewLifecycleOwner) { response ->
            Log.d("HistoryNutritionFragment", "observeHistory called with response: $response")

            if (response != null && response.data.histories != null) {
                Log.d("HistoryNutritionFragment", "observeHistory: ${response.data.histories}")
                adapter.submitList(response.data.histories)
            } else {
                Log.d("HistoryNutritionFragment", "Response is null or histories are null")
                Toast.makeText(requireContext(), "Tidak ada data untuk ditampilkan", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupRecyclerView() {
        adapter = NutritionAdapter()
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}