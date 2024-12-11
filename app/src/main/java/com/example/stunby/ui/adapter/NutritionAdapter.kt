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
import com.example.stunby.data.remote.response.HistoriesItem
import com.example.stunby.databinding.ItemHistoryBinding
import com.example.stunby.databinding.ItemNutritionBinding
import com.example.stunby.ui.detail.DetailActivity
import com.example.stunby.ui.nutrition.detail.DetailNutritionActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class NutritionAdapter : ListAdapter<HistoriesItem, NutritionAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNutritionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val nutritions = getItem(position)
        holder.bind(nutritions)

        holder.itemView.setOnClickListener {
            val idNutrition = nutritions.id
            val intent = Intent(holder.itemView.context, DetailNutritionActivity::class.java)
            intent.putExtra("key_id", idNutrition)

            Log.d("NutritionAdapter", "onBindViewHolder: $idNutrition")

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    androidx.core.util.Pair(holder.binding.tvFoodName, "food_name"),
                    androidx.core.util.Pair(holder.binding.tvPortion, "portion"),
                    androidx.core.util.Pair(holder.binding.tvDate, "date"),
                )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    class MyViewHolder(val binding: ItemNutritionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(nutritions: HistoriesItem) {

            Log.d("NutritionAdapter", "bind: $nutritions")
            binding.tvFoodName.text = nutritions.food_name
            binding.tvPortion.text = nutritions.portion.toString()
            binding.tvDate.text = nutritions.date

        }


    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<HistoriesItem> =
            object : DiffUtil.ItemCallback<HistoriesItem>() {
                override fun areItemsTheSame(oldItem: HistoriesItem, newItem: HistoriesItem): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: HistoriesItem, newItem: HistoriesItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}