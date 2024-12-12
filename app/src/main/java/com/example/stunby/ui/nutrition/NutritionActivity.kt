package com.example.stunby.ui.nutrition

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.lifecycleScope
import com.example.stunby.R
import com.example.stunby.customview.DropdownSearchComponent
import com.example.stunby.databinding.ActivityNutritionBinding
import com.example.stunby.ui.ViewModelFactory
import com.example.stunby.ui.detail.DetailActivity
import com.example.stunby.ui.nutrition.detail.DetailNutritionActivity
import com.example.stunby.ui.scan.ScanViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class NutritionActivity : AppCompatActivity() {
    lateinit var binding: ActivityNutritionBinding
    private val viewModel by viewModels<NutritionViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textInputLayout: TextInputLayout = findViewById(R.id.dropdownSearchLayout)

        val data = resources.getStringArray(R.array.food_options).toList()

        val dropdownSearch = DropdownSearchComponent(this, textInputLayout, data)
        dropdownSearch.setup()

        binding.edDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnCalculate.setOnClickListener {
            addNutrition()
        }
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        observeNutrition()
    }

    private fun observeNutrition() {
        viewModel.nutritionResult.observe(this) { result ->
            result.fold(
                onSuccess = { response ->
                    if (!response.succes!!) {
                        val measureId = response.data?.id
                        val intent = Intent(this, DetailNutritionActivity::class.java).apply {
                            putExtra("key_id", measureId)
                            // Tambahkan flag untuk menghapus fragment stack
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    } else {
                        response.message?.let { showError(it) }
                    }
                },
                onFailure = { exception ->
                    showError(exception.message ?: "Terjadi kesalahan")
                }
            )
        }
    }

    private fun addNutrition() {
        val food = binding.dropdownSearchLayout.editText?.text.toString()
        val portion = binding.edPortion.text.toString().toDouble()
        val date = binding.edDate.text.toString()



        val datePattern = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        if (!datePattern.matches(date)) {
            showError("Date must be in the format yyyy-MM-dd")
            return
        }

        viewModel.addNutrition(food, portion, date)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Format tanggal menjadi yyyy-MM-dd
            val formattedDate = "${selectedYear}-${(selectedMonth + 1).toString().padStart(2, '0')}-${selectedDay.toString().padStart(2, '0')}"
            binding.edDate.setText(formattedDate)
        }, year, month, day).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}