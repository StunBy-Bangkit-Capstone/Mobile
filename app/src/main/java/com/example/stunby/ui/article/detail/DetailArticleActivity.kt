package com.example.stunby.ui.article.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.stunby.R
import com.example.stunby.databinding.ActivityDetailArticleBinding
import com.example.stunby.ui.ViewModelFactory
import com.example.stunby.ui.detail.DetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DetailArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailArticleBinding

    private val viewModel by viewModels<DetailArticleViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val articleId = intent.getStringExtra("key_id")

        Log.d("DetailActivity", "onCreate: $articleId")
        if (articleId != null) {
            fetchArticleDetails(articleId)
        } else {
            finish()
        }

        setupObservers()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun setupObservers() {
        viewModel.article.observe(this) { article ->
            article?.let { articleData ->
                binding.articleDetailTitle.text = articleData.title
                binding.articleDetailContent.text = articleData.constent
                binding.tvAuthor.text = articleData.author

                val formattedDate = formatDate(articleData.createdAt)
                binding.tvCreatedAt.text = formattedDate



                Glide.with(this)
                    .load(articleData.articleImgUrl)
                    .into(binding.articleDetailImage)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun formatDate(inputDate: String?): String {
        if (inputDate == null) return "Tanggal tidak valid"

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

        return try {
            val date = inputFormat.parse(inputDate)
            date?.let { outputFormat.format(it) } ?: "Tanggal tidak valid"
        } catch (e: Exception) {
            "Tanggal tidak valid"
        }
    }

    private fun fetchArticleDetails(articleId: String) {
        viewModel.setLoading(true)
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getArticle(articleId)
            viewModel.setLoading(false)
        }
    }
}