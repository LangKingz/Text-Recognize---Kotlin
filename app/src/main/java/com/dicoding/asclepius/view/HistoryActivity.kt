package com.dicoding.asclepius.view

import android.content.Intent
import android.content.pm.PackageManager
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
import com.dicoding.asclepius.data.database.History
import com.dicoding.asclepius.data.database.HistoryDao
import com.dicoding.asclepius.data.database.roomHistory
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.helper.AdapterHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: AdapterHistory
    private lateinit var historydao: HistoryDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.GONE
        binding.listNew.layoutManager = LinearLayoutManager(this)

        binding.btnBack.setOnClickListener {
            finish()
        }


        val db = roomHistory.getDatabase(this)
        historydao = db.historyDao()

        loadHistory()

    }

    private fun loadHistory() {
        binding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val historyList = historydao.getAll()
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                historyAdapter = AdapterHistory(historyList)
                binding.listNew.adapter = historyAdapter
            }
        }
    }



}