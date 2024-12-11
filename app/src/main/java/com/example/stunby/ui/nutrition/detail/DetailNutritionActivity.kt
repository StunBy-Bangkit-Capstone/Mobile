package com.example.stunby.ui.nutrition.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.stunby.databinding.ActivityDetailNutritionBinding
import com.example.stunby.ui.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailNutritionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailNutritionBinding

    private val viewModel by viewModels<DetailNutritionViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nutritionId = intent.getStringExtra("key_id")

        Log.d("DetailActivity", "onCreate: $nutritionId")
        if (nutritionId != null) {
            fetchNutritionDetails(nutritionId)
        } else {
            finish()
        }

        setupObservers()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupObservers() {
        viewModel.nutrition.observe(this) { nutrition ->
            nutrition?.let { nutritionData ->
                Log.d("DetailNutritionActivity", "setupObservers: $nutritionData")
                binding.tvFoodName.text = nutritionData.food_name
                binding.tvDate.text = nutritionData.date
                binding.tvPortion.text = nutritionData.portion.toString()
                binding.tvCreatedAt.text = nutritionData.created_at
                binding.tvUpdatedAt.text = nutritionData.updated_at
                binding.tvCalories.text = nutritionData.nutrition_result?.calories.toString()
                binding.tvProteins.text = nutritionData.nutrition_result?.proteins.toString()
                binding.tvFats.text = nutritionData.nutrition_result?.fats.toString()
                binding.tvCarbohydrates.text = nutritionData.nutrition_result?.carbohydrates.toString()
                binding.tvCalcium.text = nutritionData.nutrition_result?.calciums.toString()
                binding.tvNotes.text = nutritionData.nutrition_result?.notes
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun fetchNutritionDetails(nutritionId: String) {
        viewModel.setLoading(true)
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getNutrition(nutritionId)
            viewModel.setLoading(false)
        }
    }
}