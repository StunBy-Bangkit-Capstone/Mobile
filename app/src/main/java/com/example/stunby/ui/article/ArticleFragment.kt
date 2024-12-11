package com.example.stunby.ui.article

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stunby.R
import com.example.stunby.databinding.FragmentArticleBinding
import com.example.stunby.ui.ViewModelFactory
import com.example.stunby.ui.adapter.ArticleAdapter
import com.example.stunby.ui.adapter.HistoryAdapter
import com.example.stunby.ui.home.HomeViewModel

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ArticleAdapter

    private val viewModel by viewModels<ArticleViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        observeHistory()

        return root
    }

    private fun observeHistory() {
        viewModel.article.observe(viewLifecycleOwner) { articles ->
            if (articles != null && articles.isNotEmpty()) {
                adapter.submitList(articles)
            } else {
                Toast.makeText(requireContext(), "Tidak ada cerita untuk ditampilkan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter()
        binding.articleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.articleRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}