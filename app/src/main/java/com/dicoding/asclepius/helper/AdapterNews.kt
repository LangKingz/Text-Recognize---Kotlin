package com.dicoding.asclepius.helper

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.Article
import com.dicoding.asclepius.databinding.ListhistoryBinding

class AdapterNews(private val ListNews: List<Article>) :
    RecyclerView.Adapter<AdapterNews.ViewHolder>() {
     inner class ViewHolder(private val binding: ListhistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(news : Article){
                Glide.with(itemView.context)
                    .load(news.urlToImage)
                    .into(binding.imageView)

                binding.result.text = news.title
                binding.date.text = news.description
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListhistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = ListNews.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ListNews[position])
    }
}