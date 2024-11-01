package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.database.History
import com.dicoding.asclepius.data.database.HistoryDao
import com.dicoding.asclepius.data.database.roomHistory
import com.dicoding.asclepius.databinding.ActivityResultBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var historyDao: HistoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = roomHistory.getDatabase(this)
        historyDao = db.historyDao()

       val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            binding.resultImage.setImageURI(it)
        }

        val result = intent.getStringExtra(EXTRA_RESULT)
        binding.resultText.text = result

        binding.btnFinish.setOnClickListener {
            saveHistory(imageUri.toString(), result.toString())
            finish()

        }
    }

    companion object{
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }

    private fun saveHistory(imageUri: String, result: String) {
        val history = History(
            date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            imageUri = imageUri,
            result = result,
        )
        CoroutineScope(Dispatchers.IO).launch {
            historyDao.insert(history)
            withContext(Dispatchers.Main) {
                showToast("History saved")
            }

        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}