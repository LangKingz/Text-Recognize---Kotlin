package com.dicoding.asclepius.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.ApiClient
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.dicoding.asclepius.databinding.ListhistoryBinding
import com.dicoding.asclepius.helper.AdapterHistory
import com.dicoding.asclepius.helper.AdapterNews
import kotlinx.coroutines.launch

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.listNew.layoutManager = LinearLayoutManager(this)
        binding.btnBack.setOnClickListener {
            finish()
        }
        FetchNews()
    }

    fun FetchNews(){
        lifecycleScope.launch {
            try {
                val response = ApiClient.retrofit.getNews()
                val articles = response.articles.filter { article ->
                    article.title != "[Removed]"
                }

                Log.d("NewsActivity", "fetchNews: $articles")
                binding.listNew.adapter = AdapterNews(articles)
                binding.progressBar.visibility = View.GONE

            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this@NewsActivity, "Failed to fetch news", Toast.LENGTH_SHORT).show()
            }
        }
    }
}