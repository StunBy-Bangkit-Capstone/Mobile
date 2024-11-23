package com.example.stunby.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.stunby.R
import com.example.stunby.databinding.FragmentHomeBinding
import com.example.stunby.ui.ViewModelFactory
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupUser()
        setupWHOChart()
        setupAction()
        return binding.root
    }

    private fun setupAction() {
        binding.weightLayout.setOnClickListener {
            binding.weightLayout.isSelected = true
            binding.heightLayout.isSelected = false
            updateLayoutHeight(binding.weightLayout, true)
            updateLayoutHeight(binding.heightLayout, false)
        }
        binding.heightLayout.setOnClickListener {
            binding.heightLayout.isSelected = true
            binding.weightLayout.isSelected = false
            updateLayoutHeight(binding.weightLayout, false)
            updateLayoutHeight(binding.heightLayout, true)
        }
    }

    private fun updateLayoutHeight(layout: View, isSelected: Boolean) {
        val params = layout.layoutParams as ConstraintLayout.LayoutParams
        params.height = if (isSelected) {
            resources.getDimensionPixelSize(R.dimen.selected_height) // 119dp
        } else {
            resources.getDimensionPixelSize(R.dimen.unselected_height) // 107dp
        }
        layout.layoutParams = params
    }


    private fun setupUser() {
        binding.weightLayout.isSelected = true

        viewModel.user.observe(viewLifecycleOwner) {
            binding.tvName.text = it.fullName
            Glide.with(this)
                .load(it.fotoUrl)
                .circleCrop()
                .into(binding.ivProfile)
        }
    }

    private fun setupWHOChart() {
        val chart = binding.lineChart

        chart.apply {
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(true)
            setPinchZoom(true)
            setScaleEnabled(true)
            setDrawGridBackground(true)
            maxHighlightDistance = 300f
        }

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(true)
            granularity = 1f
            axisMinimum = 0f
            axisMaximum = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()} bulan"
                }
            }
        }

        chart.axisLeft.apply {
            setDrawGridLines(true)
            axisMinimum = 0f
            axisMaximum = 14f
            granularity = 1f
        }
        chart.axisRight.isEnabled = false

        val whoData = createWHOData()
        val childEntries = listOf(
            Entry(1f, 3.5f),
            Entry(3f, 5f),
            Entry(5f, 6f),
            Entry(7f, 7.5f),
            Entry(9f, 8f),
            Entry(11f, 9f)
        )

        val childDataSet = LineDataSet(childEntries, "Berat Anak").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            circleRadius = 4f
            lineWidth = 2f
            mode = LineDataSet.Mode.LINEAR
            setDrawValues(false)
        }

        val dataSets = whoData + childDataSet
        chart.data = LineData(dataSets)
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