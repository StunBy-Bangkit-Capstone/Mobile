package com.example.stunby.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.stunby.R
import com.example.stunby.databinding.FragmentHomeBinding
import com.example.stunby.ui.ViewModelFactory
import com.example.stunby.ui.detail.DetailActivity
import com.example.stunby.ui.nutrition.NutritionActivity
import com.example.stunby.utils.WHOChartUtils
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupUser()
        setupChartSelector()
        binding.segmentedButtonGroup.check(binding.buttonOption1.id)
        binding.btnNutritionTrack.setOnClickListener {
            val intent = Intent(requireContext(), NutritionActivity::class.java).apply {
            }
            startActivity(intent)
        }
        return binding.root
    }





    private fun setupUser() {

        viewModel.user.observe(viewLifecycleOwner) {
            binding.tvName.text = it.fullName
            lifecycleScope.launch {
                val fotoUrl = withContext(Dispatchers.IO) {
                    it.fotoUrl
                }
                Glide.with(this@HomeFragment)
                    .load(fotoUrl)
                    .circleCrop()
                    .into(binding.ivProfile)
            }
        }
    }

//    private fun setupChartSelector() {
//        val chartTypes = resources.getStringArray(R.array.chart_types)
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, chartTypes)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.chartTypeSpinner.adapter = adapter
//
//        // Handle dropdown item selection
//        binding.chartTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val selectedChart = chartTypes[position]
//                setupChartData(selectedChart) // Call method to update chart
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // No action needed
//            }
//        }
//    }
//
//


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChartSelector() {
        val chartTypes = resources.getStringArray(R.array.chart_types)

        binding.segmentedButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.buttonOption1.id -> {
                        setupChartData(chartTypes[0])
                    }

                    binding.buttonOption2.id -> {
                        setupChartData(chartTypes[1])


                    }

                    binding.buttonOption3.id -> {
                        setupChartData(chartTypes[2])


                    }
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChartData(chartType: String) {
        val chart = binding.lineChart
        val sdData = getStandardDeviationData(chartType)

        viewModel.measure.observe(viewLifecycleOwner) { measures ->
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val entriesBBU = mutableListOf<Entry>()
            val entriesTBU = mutableListOf<Entry>()
            val entriesTBBB = mutableListOf<Entry>()

            measures.forEach { item ->
                val birthDay = item.user?.birthDay // Asumsi format yyyy-MM-dd
                val dateMeasure = item.dateMeasure // Asumsi format yyyy-MM-dd
                val length = (item.iMTResult?.babyLength as? Number)?.toFloat()
                val weight = item.weight?.toFloat()

                if (!birthDay.isNullOrEmpty() && !dateMeasure.isNullOrEmpty() && length != null && weight != null) {
                    try {
                        val birthDate = LocalDate.parse(birthDay, formatter)
                        val measureDate = LocalDate.parse(dateMeasure, formatter)
                        val ageInMonths = Period.between(birthDate, measureDate).toTotalMonths().toFloat()

                        entriesBBU.add(Entry(ageInMonths, weight))
                        entriesTBU.add(Entry(ageInMonths, length))
                        entriesTBBB.add(Entry(length, weight))
                    } catch (e: Exception) {
                        Log.e("ChartDataError", "Error parsing date: ${e.message}")
                    }
                }
            }

            // Mengurutkan entri berdasarkan nilai x
            entriesBBU.sortBy { it.x }
            entriesTBU.sortBy { it.x }

            // Menentukan data yang akan digunakan berdasarkan chartType
            val (childEntries, maxRange, title) = when (chartType) {
                "Berat Badan menurut Umur (BB/U)" -> Triple(entriesBBU, 24f, "Berat Badan menurut Umur (BB/U)")
                "Tinggi Badan menurut Umur (TB/U)" -> Triple(entriesTBU, 24f, "Tinggi Badan menurut Umur (TB/U)")
                "Berat Badan menurut Tinggi Badan (BB/TB)" -> Triple(entriesTBBB, 60f, "Berat Badan menurut Tinggi Badan (BB/TB)")
                else -> Triple(emptyList<Entry>(), 0f, "Unknown Chart")
            }

            // Konfigurasi dan refresh chart
            WHOChartUtils.setupWHOChart(chart, chartType, sdData, childEntries, maxRange)
            binding.tvTitle.text = title
            chart.invalidate()
        }
    }





    private fun getStandardDeviationData(chartType: String): Map<Int, List<Float>> {
        Log.d("HomeFragment", "getStandardDeviationData called with chartType: $chartType")
        return when (chartType) {
            "Berat Badan menurut Umur (BB/U)" -> mapOf(
                -3 to listOf(2.1f, 2.9f, 3.8f, 4.4f, 4.9f, 5.3f, 5.7f, 5.9f, 6.2f, 6.4f, 6.6f, 6.8f, 6.9f, 7.1f, 7.2f, 7.4f, 7.5f, 7.7f, 7.8f, 8.0f, 8.1f, 8.2f, 8.4f, 8.5f, 8.6f),
                -2 to listOf(2.5f, 3.4f, 4.3f, 5.0f, 5.6f, 6.0f, 6.4f, 6.7f, 6.9f, 7.1f, 7.4f, 7.6f, 7.7f, 7.9f, 8.1f, 8.3f, 8.4f, 8.6f, 8.8f, 8.9f, 9.1f, 9.2f, 9.4f, 9.5f, 9.7f),
                -1 to listOf(2.9f, 3.9f, 4.9f, 5.7f, 6.2f, 6.7f, 7.1f, 7.4f, 7.7f, 8.0f, 8.2f, 8.4f, 8.6f, 8.8f, 9.0f, 9.2f, 9.4f, 9.6f, 9.8f, 10.0f, 10.1f, 10.3f, 10.5f, 10.7f, 10.8f),
                0 to listOf(3.3f, 4.5f, 5.6f, 6.4f, 7.0f, 7.5f, 7.9f, 8.3f, 8.6f, 8.9f, 9.2f, 9.4f, 9.6f, 9.9f, 10.1f, 10.3f, 10.5f, 10.7f, 10.9f, 11.1f, 11.3f, 11.5f, 11.8f, 12.0f, 12.2f),
                1 to listOf(3.9f, 5.1f, 6.3f, 7.2f, 7.8f, 8.4f, 8.8f, 9.2f, 9.6f, 9.9f, 10.2f, 10.5f, 10.8f, 11.0f, 11.3f, 11.5f, 11.7f, 12.0f, 12.2f, 12.5f, 12.7f, 12.9f, 13.2f, 13.4f, 13.6f),
                2 to listOf(4.4f, 5.8f, 7.1f, 8.0f, 8.7f, 9.3f, 9.8f, 10.3f, 10.7f, 11.0f, 11.4f, 11.7f, 12.0f, 12.3f, 12.6f, 12.8f, 13.1f, 13.4f, 13.7f, 13.9f, 14.2f, 14.5f, 14.7f, 15.0f, 15.3f),
                3 to listOf(5.0f, 6.6f, 8.0f, 9.0f, 9.7f, 10.4f, 10.9f, 11.4f, 11.9f, 12.3f, 12.7f, 13.0f, 13.3f, 13.7f, 14.0f, 14.3f, 14.6f, 14.9f, 15.3f, 15.6f, 15.9f, 16.2f, 16.5f, 16.8f, 17.1f)
            )
            "Tinggi Badan menurut Umur (TB/U)" -> mapOf(
                -3 to listOf(44.2f, 48.9f, 52.4f, 55.3f, 57.6f, 59.6f, 61.2f, 62.7f, 64.0f, 65.2f, 66.4f, 67.6f, 68.6f, 69.6f, 70.6f, 71.6f, 72.5f, 73.3f, 74.2f, 75.0f, 75.8f, 76.5f, 77.2f, 78.0f),
                -2 to listOf(46.1f, 50.8f, 54.4f, 57.3f, 59.7f, 61.7f, 63.3f, 64.8f, 66.2f, 67.5f, 68.7f, 69.9f, 71.0f, 72.1f, 73.1f, 74.1f, 75.0f, 76.0f, 76.9f, 77.7f, 78.6f, 79.4f, 80.2f, 81.0f),
                -1 to listOf(48.0f, 52.8f, 56.4f, 59.4f, 61.8f, 63.8f, 65.5f, 67.0f, 68.4f, 69.7f, 71.0f, 72.2f, 73.4f, 74.5f, 75.6f, 76.6f, 77.6f, 78.6f, 79.6f, 80.5f, 81.4f, 82.3f, 83.1f, 83.9f),
                0 to listOf(49.9f, 54.7f, 58.4f, 61.4f, 63.9f, 65.9f, 67.6f, 69.2f, 70.6f, 72.0f, 73.3f, 74.5f, 75.7f, 76.9f, 78.0f, 79.1f, 80.2f, 81.2f, 82.3f, 83.2f, 84.2f, 85.1f, 86.0f, 86.9f),
                1 to listOf(51.8f, 56.7f, 60.4f, 63.5f, 66.0f, 68.0f, 69.8f, 71.3f, 72.8f, 74.2f, 75.6f, 76.9f, 78.1f, 79.3f, 80.5f, 81.7f, 82.8f, 83.9f, 85.0f, 86.0f, 87.0f, 88.0f, 89.0f, 89.9f),
                2 to listOf(53.7f, 58.6f, 62.4f, 65.5f, 68.0f, 70.1f, 71.9f, 73.5f, 75.0f, 76.5f, 77.9f, 79.2f, 80.5f, 81.8f, 83.0f, 84.2f, 85.4f, 86.5f, 87.7f, 88.8f, 89.8f, 90.9f, 91.9f, 92.9f),
                3 to listOf(55.6f, 60.6f, 64.4f, 67.6f, 70.1f, 72.2f, 74.0f, 75.7f, 77.2f, 78.7f, 80.1f, 81.5f, 82.9f, 84.2f, 85.5f, 86.7f, 88.0f, 89.2f, 90.4f, 91.5f, 92.6f, 93.8f, 94.9f, 95.9f)
            )
            "Berat Badan menurut Tinggi Badan (BB/TB)" -> mapOf(
                -3 to listOf(
                    1.9f, 1.9f, 2.0f, 2.1f, 2.1f, 2.2f, 2.3f, 2.3f, 2.4f, 2.5f,
                    2.6f, 2.7f, 2.7f, 2.8f, 2.9f, 3.0f, 3.1f, 3.2f, 3.3f, 3.4f,
                    3.6f, 3.7f, 3.8f, 3.9f, 4.0f, 4.1f, 4.3f, 4.4f, 4.5f, 4.6f,
                    4.7f, 4.8f, 4.9f, 5.0f, 5.1f, 5.2f, 5.3f, 5.4f, 5.5f, 5.6f,
                    5.7f, 5.8f, 5.9f, 6.0f, 6.1f, 6.2f, 6.3f, 6.4f, 6.5f, 6.6f,
                    6.6f, 6.7f, 6.8f, 6.9f, 7.0f, 7.1f, 7.2f, 7.2f, 7.3f, 7.4f,
                    7.5f, 7.6f, 7.6f, 7.7f, 7.8f, 7.9f, 7.9f, 8.0f, 8.1f, 8.2f,
                    8.2f, 8.3f, 8.4f, 8.5f, 8.5f, 8.6f, 8.7f, 8.8f, 8.9f, 9.0f,
                    9.1f, 9.2f, 9.3f, 9.4f, 9.5f, 9.6f, 9.7f, 9.8f, 9.9f, 10.0f,
                    10.1f, 10.2f, 10.3f, 10.4f, 10.5f, 10.6f, 10.7f, 10.7f, 10.8f,
                    10.9f, 11.0f, 11.1f, 11.2f, 11.3f, 11.4f, 11.5f, 11.6f, 11.7f,
                    11.8f, 11.9f, 12.0f, 12.1f, 12.2f, 12.3f, 12.4f, 12.5f, 12.6f,
                    12.7f, 12.8f, 12.9f, 13.0f, 13.2f, 13.3f, 13.4f, 13.5f, 13.6f,
                    13.7f, 13.8f, 14.0f, 14.1f, 14.2f
                ),

                -2 to listOf(
                    2.0f, 2.1f, 2.2f, 2.3f, 2.3f, 2.4f, 2.5f, 2.6f, 2.6f, 2.7f,
                    2.8f, 2.9f, 3.0f, 3.1f, 3.2f, 3.3f, 3.4f, 3.5f, 3.6f, 3.7f,
                    3.8f, 4.0f, 4.1f, 4.2f, 4.3f, 4.5f, 4.6f, 4.7f, 4.8f, 5.0f,
                    5.1f, 5.2f, 5.3f, 5.4f, 5.6f, 5.7f, 5.8f, 5.9f, 6.0f, 6.1f,
                    6.2f, 6.3f, 6.4f, 6.5f, 6.6f, 6.7f, 6.8f, 6.9f, 7.0f, 7.1f,
                    7.2f, 7.3f, 7.4f, 7.5f, 7.6f, 7.6f, 7.7f, 7.8f, 7.9f, 8.0f,
                    8.1f, 8.2f, 8.3f, 8.3f, 8.4f, 8.5f, 8.6f, 8.7f, 8.7f, 8.8f,
                    8.9f, 9.0f, 9.1f, 9.1f, 9.2f, 9.3f, 9.4f, 9.5f, 9.6f, 9.7f,
                    9.8f, 9.9f, 10.0f, 10.1f, 10.2f, 10.4f, 10.5f, 10.6f, 10.7f,
                    10.8f, 10.9f, 11.0f, 11.1f, 11.2f, 11.3f, 11.4f, 11.5f, 11.6f,
                    11.7f, 11.8f, 11.9f, 12.0f, 12.1f, 12.2f, 12.3f, 12.4f, 12.5f,
                    12.6f, 12.7f, 12.8f, 12.9f, 13.0f, 13.2f, 13.3f, 13.4f, 13.5f,
                    13.6f, 13.7f, 13.9f, 14.0f, 14.1f, 14.2f, 14.4f, 14.5f, 14.6f,
                    14.7f, 14.9f, 15.0f, 15.1f, 15.3f, 15.4f
                ),

                -1 to listOf(
                    2.2f, 2.3f, 2.4f, 2.5f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 3.0f,
                    3.0f, 3.1f, 3.2f, 3.3f, 3.5f, 3.6f, 3.7f, 3.8f, 3.9f, 4.0f,
                    4.2f, 4.3f, 4.4f, 4.6f, 4.7f, 4.9f, 5.0f, 5.1f, 5.3f, 5.4f,
                    5.5f, 5.6f, 5.8f, 5.9f, 6.0f, 6.1f, 6.2f, 6.4f, 6.5f, 6.6f,
                    6.7f, 6.8f, 6.9f, 7.0f, 7.1f, 7.2f, 7.3f, 7.5f, 7.6f, 7.7f,
                    7.8f, 7.9f, 8.0f, 8.1f, 8.2f, 8.3f, 8.4f, 8.5f, 8.6f, 8.7f,
                    8.8f, 8.8f, 8.9f, 9.0f, 9.1f, 9.2f, 9.3f, 9.4f, 9.5f, 9.5f,
                    9.6f, 9.7f, 9.8f, 9.9f, 10.0f, 10.1f, 10.2f, 10.3f, 10.4f,
                    10.5f, 10.6f, 10.7f, 10.8f, 11.0f, 11.1f, 11.2f, 11.3f, 11.4f,
                    11.5f, 11.6f, 11.8f, 11.9f, 12.0f, 12.1f, 12.2f, 12.3f, 12.4f,
                    12.5f, 12.6f, 12.7f, 12.8f, 12.9f, 13.1f, 13.2f, 13.3f, 13.4f,
                    13.5f, 13.6f, 13.7f, 13.9f, 14.0f, 14.1f, 14.2f, 14.4f, 14.5f,
                    14.6f, 14.8f, 14.9f, 15.0f, 15.2f, 15.3f, 15.4f, 15.6f, 15.7f,
                    15.9f, 16.0f, 16.2f, 16.3f, 16.5f, 16.6f, 16.8f
                ),
                0 to listOf(
                    2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.9f, 3.0f, 3.1f, 3.2f,
                    3.3f, 3.4f, 3.5f, 3.6f, 3.8f, 3.9f, 4.0f, 4.1f, 4.3f, 4.4f,
                    4.5f, 4.7f, 4.8f, 5.0f, 5.1f, 5.3f, 5.4f, 5.6f, 5.7f, 5.9f,
                    6.0f, 6.1f, 6.3f, 6.4f, 6.5f, 6.7f, 6.8f, 6.9f, 7.0f, 7.1f,
                    7.3f, 7.4f, 7.5f, 7.6f, 7.7f, 7.9f, 8.0f, 8.1f, 8.2f, 8.3f,
                    8.4f, 8.5f, 8.6f, 8.8f, 8.9f, 9.0f, 9.1f, 9.2f, 9.3f, 9.4f,
                    9.5f, 9.6f, 9.7f, 9.8f, 9.9f, 10.0f, 10.1f, 10.2f, 10.3f, 10.4f,
                    10.4f, 10.5f, 10.6f, 10.7f, 10.8f, 10.9f, 11.0f, 11.2f, 11.3f,
                    11.4f, 11.5f, 11.6f, 11.7f, 11.9f, 12.0f, 12.1f, 12.2f, 12.4f,
                    12.5f, 12.6f, 12.7f, 12.8f, 13.0f, 13.1f, 13.2f, 13.3f, 13.4f,
                    13.5f, 13.7f, 13.8f, 13.9f, 14.0f, 14.1f, 14.3f, 14.4f, 14.5f,
                    14.6f, 14.8f, 14.9f, 15.0f, 15.2f, 15.3f, 15.4f, 15.6f, 15.7f,
                    15.9f, 16.0f, 16.2f, 16.3f, 16.5f, 16.6f, 16.8f, 16.9f, 17.1f,
                    17.3f, 17.4f, 17.6f, 17.8f, 17.9f, 18.1f, 18.3f
                ),
                1 to listOf(
                    2.7f, 2.8f, 2.9f, 3.0f, 3.0f, 3.1f, 3.2f, 3.3f, 3.4f, 3.5f,
                    3.6f, 3.8f, 3.9f, 4.0f, 4.1f, 4.2f, 4.4f, 4.5f, 4.7f, 4.8f,
                    5.0f, 5.1f, 5.3f, 5.4f, 5.6f, 5.7f, 5.9f, 6.1f, 6.2f, 6.4f,
                    6.5f, 6.7f, 6.8f, 7.0f, 7.1f, 7.2f, 7.4f, 7.5f, 7.6f, 7.8f,
                    7.9f, 8.0f, 8.2f, 8.3f, 8.4f, 8.5f, 8.7f, 8.8f, 8.9f, 9.0f,
                    9.2f, 9.3f, 9.4f, 9.5f, 9.6f, 9.8f, 9.9f, 10.0f, 10.1f, 10.2f,
                    10.3f, 10.4f, 10.6f, 10.7f, 10.8f, 10.9f, 11.0f, 11.1f, 11.2f,
                    11.3f, 11.4f, 11.5f, 11.6f, 11.7f, 11.8f, 11.9f, 12.0f, 12.1f,
                    12.2f, 12.4f, 12.5f, 12.6f, 12.8f, 12.9f, 13.0f, 13.2f, 13.3f,
                    13.4f, 13.5f, 13.7f, 13.8f, 13.9f, 14.1f, 14.2f, 14.3f, 14.4f,
                    14.6f, 14.7f, 14.8f, 14.9f, 15.1f, 15.2f, 15.3f, 15.5f, 15.6f,
                    15.7f, 15.9f, 16.0f, 16.2f, 16.3f, 16.5f, 16.6f, 16.8f, 16.9f,
                    17.1f, 17.3f, 17.4f, 17.6f, 17.8f, 17.9f, 18.1f, 18.3f, 18.5f,
                    18.6f, 18.8f, 19.0f, 19.2f, 19.4f, 19.6f, 19.8f, 20.0f
                ),
                2 to listOf(
                    3.0f, 3.1f, 3.1f, 3.2f, 3.3f, 3.4f, 3.6f, 3.7f, 3.8f, 3.9f,
                    4.0f, 4.1f, 4.2f, 4.4f, 4.5f, 4.6f, 4.8f, 4.9f, 5.1f, 5.3f,
                    5.4f, 5.6f, 5.8f, 5.9f, 6.1f, 6.3f, 6.4f, 6.6f, 6.8f, 7.0f,
                    7.1f, 7.3f, 7.4f, 7.6f, 7.7f, 7.9f, 8.0f, 8.2f, 8.3f, 8.5f,
                    8.6f, 8.7f, 8.9f, 9.0f, 9.2f, 9.3f, 9.4f, 9.6f, 9.7f, 9.8f,
                    10.0f, 10.1f, 10.2f, 10.4f, 10.5f, 10.6f, 10.8f, 10.9f, 11.0f,
                    11.2f, 11.3f, 11.4f, 11.5f, 11.6f, 11.7f, 11.9f, 12.0f, 12.1f,
                    12.2f, 12.3f, 12.4f, 12.5f, 12.6f, 12.7f, 12.8f, 13.0f, 13.1f,
                    13.2f, 13.3f, 13.5f, 13.6f, 13.7f, 13.9f, 14.0f, 14.2f, 14.3f,
                    14.5f, 14.6f, 14.7f, 14.9f, 15.0f, 15.1f, 15.3f, 15.4f, 15.6f,
                    15.7f, 15.8f, 16.0f, 16.1f, 16.3f, 16.4f, 16.5f, 16.7f, 16.8f,
                    17.0f, 17.1f, 17.3f, 17.5f, 17.6f, 17.8f, 18.0f, 18.1f, 18.3f,
                    18.5f, 18.7f, 18.8f, 19.0f, 19.2f, 19.4f, 19.6f, 19.8f, 20.0f,
                    20.2f, 20.4f, 20.6f, 20.8f, 21.0f, 21.2f, 21.4f, 21.7f, 21.9f
                ),
                3 to listOf(
                    3.3f, 3.4f, 3.5f, 3.6f, 3.7f, 3.8f, 3.9f, 4.0f, 4.2f, 4.3f,
                    4.4f, 4.5f, 4.7f, 4.8f, 5.0f, 5.1f, 5.3f, 5.4f, 5.6f, 5.8f,
                    6.0f, 6.1f, 6.3f, 6.5f, 6.7f, 6.9f, 7.1f, 7.2f, 7.4f, 7.6f,
                    7.8f, 8.0f, 8.1f, 8.3f, 8.5f, 8.6f, 8.8f, 8.9f, 9.1f, 9.3f,
                    9.4f, 9.6f, 9.7f, 9.9f, 10.0f, 10.2f, 10.3f, 10.5f, 10.6f, 10.8f,
                    10.9f, 11.1f, 11.2f, 11.3f, 11.5f, 11.6f, 11.8f, 11.9f, 12.1f,
                    12.2f, 12.3f, 12.5f, 12.6f, 12.7f, 12.8f, 13.0f, 13.1f, 13.2f,
                    13.3f, 13.4f, 13.6f, 13.7f, 13.8f, 13.9f, 14.0f, 14.2f, 14.3f,
                    14.4f, 14.6f, 14.7f, 14.9f, 15.0f, 15.2f, 15.3f, 15.5f, 15.6f,
                    15.8f, 15.9f, 16.1f, 16.2f, 16.4f, 16.5f, 16.7f, 16.8f, 17.0f,
                    17.1f, 17.3f, 17.4f, 17.6f, 17.7f, 17.9f, 18.0f, 18.2f, 18.4f,
                    18.5f, 18.7f, 18.9f, 19.1f, 19.2f, 19.4f, 19.6f, 19.8f, 20.0f,
                    20.2f, 20.4f, 20.6f, 20.8f, 21.0f, 21.2f, 21.5f, 21.7f, 21.9f,
                    22.1f, 22.4f, 22.6f, 22.8f, 23.1f, 23.3f, 23.6f, 23.8f, 24.1f
                )
            )

            // Add other data mappings
            else -> emptyMap()
        }
    }


    private fun setupWHOChart(dataSet: List<LineDataSet>) {
        val chart = binding.lineChart
        chart.data = LineData(dataSet)
        chart.invalidate()
    }



    private fun createWHOData(): List<LineDataSet> {
        // WHO Standard Deviation curves (SD)
        val sd3plus = createSDCurve(3)
        val sd2plus = createSDCurve(2)
        val sd0 = createSDCurve(0)
        val sd2minus = createSDCurve(-2)
        val sd3minus = createSDCurve(-3)

        // Create datasets with different styles
        val upperLimit = LineDataSet(sd3plus, "+3 SD").apply {
            color = Color.RED
            lineWidth = 1.5f
            setDrawCircles(false)
            setDrawValues(false)

        }

        val normalUpperLimit = LineDataSet(sd2plus, "+2 SD").apply {
            color = Color.YELLOW
            lineWidth = 1.5f
            setDrawCircles(false)
            setDrawValues(false)

        }

        val median = LineDataSet(sd0, "Median").apply {
            color = Color.GREEN
            lineWidth = 1.5f
            setDrawCircles(false)
            setDrawValues(false)

            enableDashedLine(10f, 5f, 0f)

        }

        val normalLowerLimit = LineDataSet(sd2minus, "-2 SD").apply {
            color = Color.YELLOW
            lineWidth = 1.5f
            setDrawCircles(false)
            setDrawValues(false)

        }

        val lowerLimit = LineDataSet(sd3minus, "-3 SD").apply {
            color = Color.RED
            lineWidth = 1.5f
            setDrawCircles(false)
            setDrawValues(false)
            setDrawFilled(false)
        }

        return listOf(upperLimit, normalUpperLimit, median, normalLowerLimit, lowerLimit)
    }

    private fun createSDCurve(sd: Int): List<Entry> {
        // Data WHO untuk setiap SD
        val whoData = mapOf(
            -3 to listOf(2.1f, 2.9f, 3.8f, 4.4f, 4.9f, 5.3f, 5.7f, 5.9f, 6.2f, 6.4f, 6.6f, 6.8f, 6.9f),
            -2 to listOf(2.5f, 3.4f, 4.3f, 5.0f, 5.6f, 6.0f, 6.4f, 6.7f, 6.9f, 7.1f, 7.4f, 7.6f, 7.7f),
            -1 to listOf(2.9f, 3.9f, 4.9f, 5.7f, 6.2f, 6.7f, 7.1f, 7.4f, 7.7f, 8.0f, 8.2f, 8.4f, 8.6f),
            0 to listOf(3.3f, 4.5f, 5.6f, 6.4f, 7.0f, 7.5f, 7.9f, 8.3f, 8.6f, 8.9f, 9.2f, 9.4f, 9.6f),
            1 to listOf(3.9f, 5.1f, 6.3f, 7.2f, 7.8f, 8.4f, 8.8f, 9.2f, 9.6f, 9.9f, 10.2f, 10.5f, 10.8f),
            2 to listOf(4.4f, 5.8f, 7.1f, 8.0f, 8.7f, 9.3f, 9.8f, 10.3f, 10.7f, 11.0f, 11.4f, 11.7f, 12.0f),
            3 to listOf(5.0f, 6.6f, 8.0f, 9.0f, 9.7f, 10.4f, 10.9f, 11.4f, 11.9f, 12.3f, 12.7f, 13.0f, 13.3f)
        )

        val data = whoData[sd] ?: return emptyList()
        return data.mapIndexed { index, value -> Entry(index.toFloat(), value) }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}