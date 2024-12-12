package com.example.stunby.ui.detail

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.stunby.R
import com.example.stunby.data.remote.response.Data
import com.example.stunby.data.remote.response.DataDetail
import com.example.stunby.data.remote.response.DataUser
import com.example.stunby.databinding.ActivityDetailBinding
import com.example.stunby.ui.ViewModelFactory
import com.example.stunby.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val measureId = intent.getStringExtra("key_id")

        Log.d("DetailActivity", "onCreate: $measureId")
        if (measureId != null) {
            fetchMeasureDetails(measureId)
        } else {
            finish()
        }
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }


        setupObservers()

    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java).apply {
            // Membersihkan stack dan memastikan MainActivity menjadi satu-satunya aktivitas
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "history") // Untuk navigasi ke fragment History
        }
        startActivity(intent)
        finish()
    }




    private fun fetchMeasureDetails(measureId: String) {
        viewModel.setLoading(true)
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getMeasure(measureId)
            viewModel.setLoading(false)
        }
    }

    private fun setupObservers() {
        viewModel.measure.observe(this) { measure ->
            measure?.let { measureData ->
                viewModel.user.observe(this) { user ->
                    user?.let { userData ->
                        displayMeasureDetails(measureData, userData)
                    }
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }


    private fun displayMeasureDetails(measure: Data, user: DataUser) {
        binding.apply {

            tvName.text = user.fullName
            tvGender.text = user.gender

            val birthDate = user.birthDay
            calculateAge(birthDate, measure.dateMeasure)

            Log.d("DetailActivity", "displayMeasureDetails: ${measure.babyPhotoUrl}")
            Glide.with(this@DetailActivity)
                .load(measure.babyPhotoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                .into(ivDetailPhoto)


            edWeight.text = Editable.Factory.getInstance().newEditable(getString(R.string.berat_bayi, "${measure.weight}"))
            edHeight.text = Editable.Factory.getInstance().newEditable(getString(R.string.panjang_bayi, "${measure.iMTResult?.babyLength}"))


            edLevel.text = Editable.Factory.getInstance().newEditable(getString(R.string.level_aktivitas, measure.levelActivity))
            edAsi.text = Editable.Factory.getInstance().newEditable(getString(R.string.status_asi, measure.statusAsi))


            tvKarbo.text = measure.measuremenetResult!!.carbohydrateNeeded.toString()
            tvProtein.text = measure.measuremenetResult.proteinNeeded.toString()
            tvLemak.text = measure.measuremenetResult.fatNeeded.toString()
            tvKalori.text = measure.measuremenetResult.caloriesNeeded.toString()

            tvZScoreBBTB.text = getString(R.string.z_score_bbtb, String.format(Locale.getDefault(), "%.2f", measure.iMTResult?.zScoreBbTb ?: 0.0))
            tvZScoreLength.text = getString(R.string.z_score_length, String.format(Locale.getDefault(), "%.2f", measure.iMTResult?.zScoreLength ?: 0.0))
            tvZScoreWeight.text = getString(R.string.z_score_weight, String.format(Locale.getDefault(), "%.2f", measure.iMTResult?.zScoreWeight ?: 0.0))


            tvStatusBBTB.text = measure.iMTResult?.statusBbTb
            tvNutritionalStatusWeight.text = measure.iMTResult?.nitritionalStatusWeight
            tvNutritionalStatusLength.text = measure.iMTResult?.nitritionalStatusLength
            tvStatusIMT.text = measure.iMTResult?.statusImt


        }
    }

    private fun formatDate(inputDate: String?): String {
        if (inputDate == null) return "Tanggal tidak valid"

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        return try {
            val date = inputFormat.parse(inputDate)
            date?.let { outputFormat.format(it) } ?: "Tanggal tidak valid"
        } catch (e: Exception) {
            "Tanggal tidak valid"
        }
    }



    private fun calculateAge(birthDate: String?, dateMeasure: String?) {
        if (birthDate == null || dateMeasure == null) {
            binding.tvAge.text = "Tanggal tidak valid"
            return
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthLocalDate = LocalDate.parse(birthDate, formatter)
        val measureLocalDate = LocalDate.parse(dateMeasure, formatter)

        val years = ChronoUnit.YEARS.between(birthLocalDate, measureLocalDate).toInt()
        val months = ChronoUnit.MONTHS.between(birthLocalDate.plusYears(years.toLong()), measureLocalDate).toInt()
        val days = ChronoUnit.DAYS.between(birthLocalDate.plusYears(years.toLong()).plusMonths(months.toLong()), measureLocalDate).toInt()
        val ageString = if (years == 0 && months == 0) {
            "$days Hari"
        } else if (years == 0) {
            "$months Bulan $days Hari"
        } else {
            "$years Tahun $months Bulan $days Hari"
        }
        binding.tvAge.text = ageString
    }
}