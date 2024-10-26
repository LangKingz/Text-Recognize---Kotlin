package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityResultBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

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

        binding.btnTransalate.setOnClickListener{
            binding.progressBar2.visibility = View.VISIBLE
            transalateText(result)
        }

    }

//    fungsi transalate text

    private fun transalateText(text: String?){
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.INDONESIAN)
            .build()
        val indonesiaEnglishTransaltor =Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        indonesiaEnglishTransaltor.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                indonesiaEnglishTransaltor.translate(text.toString())
                    .addOnSuccessListener {
                        transaltedText ->
                        binding.ResultTransalte.text = transaltedText
                        binding.progressBar2.visibility = View.GONE
                        indonesiaEnglishTransaltor.close()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Failed to transalate text", Toast.LENGTH_SHORT).show()
                        binding.progressBar2.visibility = View.GONE
                        indonesiaEnglishTransaltor.close()
                    }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to download model", Toast.LENGTH_SHORT).show()
                binding.progressBar2.visibility = View.GONE
            }

    }


    companion object{
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}