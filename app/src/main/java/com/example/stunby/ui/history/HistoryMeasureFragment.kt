package com.example.stunby.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunby.databinding.FragmentHistoryMeasureBinding
import com.example.stunby.ui.ViewModelFactory
import com.example.stunby.ui.adapter.HistoryAdapter

class HistoryMeasureFragment : Fragment() {

    private var _binding: FragmentHistoryMeasureBinding? = null
    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: HistoryAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryMeasureBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupRecyclerView()
        observeHistory()

        return root
    }

    private fun observeHistory() {
        viewModel.measure.observe(viewLifecycleOwner) { measures ->
            if (measures != null && measures.isNotEmpty()) {
                adapter.submitList(measures)
            } else {
                Toast.makeText(requireContext(), "Tidak ada cerita untuk ditampilkan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter()
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}