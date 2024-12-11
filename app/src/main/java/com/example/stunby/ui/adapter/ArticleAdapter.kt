package com.example.stunby.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stunby.data.remote.response.DataItemArticle
import com.example.stunby.databinding.ItemArticleBinding
import com.example.stunby.ui.article.detail.DetailArticleActivity
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleAdapter : ListAdapter<DataItemArticle, ArticleAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val articles = getItem(position)
        holder.bind(articles)

        holder.itemView.setOnClickListener {
            val idArticle = articles.id
            val intent = Intent(holder.itemView.context, DetailArticleActivity::class.java)
            intent.putExtra("key_id", idArticle)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    androidx.core.util.Pair(holder.binding.articleTitle, "title"),
                    androidx.core.util.Pair(holder.binding.articleImage, "image"),
                    androidx.core.util.Pair(holder.binding.articleCreatedAt, "created_at"),
                    androidx.core.util.Pair(holder.binding.articleContentPreview, "content"),
                )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    class MyViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(articles: DataItemArticle) {

            binding.articleTitle.text = articles.title
            binding.articleContentPreview.text = articles.constent

            val formattedDate = formatDate(articles.createdAt)
            binding.articleCreatedAt.text = formattedDate


            Glide.with(itemView.context)
                .load(articles.articleImgUrl)
                .into(binding.articleImage)

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


    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DataItemArticle> =
            object : DiffUtil.ItemCallback<DataItemArticle>() {
                override fun areItemsTheSame(oldItem: DataItemArticle, newItem: DataItemArticle): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: DataItemArticle, newItem: DataItemArticle): Boolean {
                    return oldItem == newItem
                }
            }
    }
}