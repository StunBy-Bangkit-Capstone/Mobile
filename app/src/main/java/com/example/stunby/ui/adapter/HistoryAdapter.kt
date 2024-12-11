package com.example.stunby.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.stunby.data.remote.response.DataItem
import com.example.stunby.databinding.ItemHistoryBinding
import com.example.stunby.ui.detail.DetailActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class HistoryAdapter  : ListAdapter<DataItem, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val histories = getItem(position)
        holder.bind(histories)

        holder.itemView.setOnClickListener {
            val idStory = histories.id
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("key_id", idStory)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    androidx.core.util.Pair(holder.binding.tvStatus, "status"),
                    androidx.core.util.Pair(holder.binding.tvItemHeight, "height"),
                    androidx.core.util.Pair(holder.binding.tvItemWeight, "weight"),
                    androidx.core.util.Pair(holder.binding.tvItemDate, "date"),
                )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    class MyViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(measures: DataItem) {

            binding.tvStatus.text = measures.iMTResult?.statusImt
            binding.tvItemHeight.text = measures.iMTResult?.babyLength.toString()
            binding.tvItemWeight.text = measures.weight.toString()
            binding.tvItemDate.text = formatDate(measures.dateMeasure)
            val birthDate = measures.user?.birthDay
            calculateAge(birthDate, measures.dateMeasure)


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
                binding.tvItemAge.text = "Tanggal tidak valid"
                return
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val birthLocalDate = LocalDate.parse(birthDate, formatter)
            val measureLocalDate = LocalDate.parse(dateMeasure, formatter)

            val years = ChronoUnit.YEARS.between(birthLocalDate, measureLocalDate).toInt()
            val months = ChronoUnit.MONTHS.between(birthLocalDate.plusYears(years.toLong()), measureLocalDate).toInt()
            val days = ChronoUnit.DAYS.between(birthLocalDate.plusYears(years.toLong()).plusMonths(months.toLong()), measureLocalDate).toInt()

            val ageString = "$years Tahun $months Bulan $days Hari"
            binding.tvItemAge.text = ageString
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DataItem> =
            object : DiffUtil.ItemCallback<DataItem>() {
                override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}