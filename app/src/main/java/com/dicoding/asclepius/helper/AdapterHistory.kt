package com.dicoding.asclepius.helper

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.database.History
import com.dicoding.asclepius.databinding.ListhistoryBinding

class AdapterHistory(private val listHistory: List<History>) :
    RecyclerView.Adapter<AdapterHistory.ViewHolder>() {

    inner class ViewHolder(private val binding: ListhistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History){
            Glide.with(itemView.context)
                .load(Uri.parse(history.imageUri))
                .into(binding.imageView)
            binding.result.text = history.result
            binding.date.text = history.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListhistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listHistory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position >= 0 && position < listHistory.size) {
            holder.bind(listHistory[position])
        } else {
            Log.e("AdapterHistory", "Invalid position: $position")
        }
    }
}