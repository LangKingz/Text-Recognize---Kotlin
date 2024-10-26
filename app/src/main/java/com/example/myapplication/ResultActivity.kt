package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ImageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        ImageUri?.let {
            Log.d("ResultActivity", "Image URI: $it")
            binding.resultImage.setImageURI(it)
        }

        val result = intent.getStringExtra(EXTRA_RESULT)
        result?.let {
            binding.resultText.text = it
        }

    }
    companion object{
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}