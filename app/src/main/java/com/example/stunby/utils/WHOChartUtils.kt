package com.example.stunby.utils

import android.graphics.Color
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.charts.LineChart

object WHOChartUtils {

    fun setupWHOChart(
        chart: LineChart,
        chartType: String,
        sdData: Map<Int, List<Float>>,
        childEntries: List<Entry>,
        axisMonth: Float,
    ) {
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(true)
            setPinchZoom(true)
            setScaleEnabled(true)
            setDragEnabled(true)
            setDrawGridBackground(false) // Remove grid background for a cleaner look
            maxHighlightDistance = 300f
            animateX(1500) // Animates both X and Y axes for 1.5 seconds

        }

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(true)
            setDrawAxisLine(true)
            setDrawLabels(true)
            granularity = 1f
            axisMinimum = 0f
            axisMaximum = axisMonth
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (chartType.contains("Umur")) "${value.toInt()} bln" else "${value.toInt()} cm"
                }
            }
            textColor = Color.BLACK
            textSize = 12f
        }

        chart.axisLeft.apply {
            setDrawGridLines(true)
            gridColor = Color.LTGRAY // Lighter grid lines for a modern look
            gridLineWidth = 0.8f
            axisMinimum = (sdData.values.flatten().minOrNull() ?: 0f) - 5f
            axisMaximum = (sdData.values.flatten().maxOrNull() ?: 100f) + 5f
            granularity = 1f
            textColor = Color.BLACK
            textSize = 12f
        }

        chart.axisRight.isEnabled = false // Disable right axis for a cleaner layout

        // Create WHO Data
        val whoData = createWHOData(sdData)

        // Child Data Set (example with entries)
        val childDataSet = LineDataSet(childEntries, "Data Anak").apply {
            color = Color.parseColor("#2196F3") // Modern blue
            setCircleColor(Color.parseColor("#2196F3"))
            circleRadius = 5f
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth line
            setDrawValues(false)
            setDrawCircles(true)
        }

        val dataSets = whoData + childDataSet
        chart.data = LineData(dataSets)

        // Apply smooth animation for modern feel


        // Apply zoom only once, for the default zoom level
        chart.zoom(1f, 1f, 0f, 0f) // Default zoom behavior, can be adjusted if needed

        chart.invalidate() // Refresh the chart
    }


    private fun createWHOData(sdData: Map<Int, List<Float>>): List<LineDataSet> {
        return sdData.map { (sd, values) ->
            val label = when (sd) {
                3 -> "+3 SD"
                2 -> "+2 SD"
                0 -> "Median"
                -2 -> "-2 SD"
                -3 -> "-3 SD"
                else -> "SD $sd"
            }

            val color = when (sd) {
                3, -3 -> Color.parseColor("#F44336") // Red for extreme SD
                2, -2 -> Color.parseColor("#FFEB3B") // Yellow for moderate SD
                0 -> Color.parseColor("#4CAF50") // Green for median
                else -> Color.GRAY // Default color for other cases
            }

            LineDataSet(values.mapIndexed { index, value -> Entry(index.toFloat(), value) }, label).apply {
                this.color = color
                lineWidth = 2f
                setDrawCircles(false)
                setDrawValues(false)
                if (sd == 0) {
                    enableDashedLine(10f, 5f, 0f) // Dashed line for the median
                }
            }
        }
    }
}
