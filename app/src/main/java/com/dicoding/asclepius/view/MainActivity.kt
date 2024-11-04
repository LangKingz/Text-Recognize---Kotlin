package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.data.database.roomHistory
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnNews.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }

        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                binding.progressIndicator.visibility = View.VISIBLE
                imageClassifierHelper = ImageClassifierHelper(
                    context = this,
                    classifierListener = object : ImageClassifierHelper.ClassifierListener {
                        override fun onResult(result: List<Classifications>?) {
                            Log.d("RESULT", "Classification Result: $result")
                            if (result != null && result.isNotEmpty() && result[0].categories.size >= 2) {
                                val cancerProbability = result[0].categories[0].score
                                // Hitung persentase
                                val cancerPercentage = (cancerProbability * 100).toInt()

                                val resulText = result[0].categories[0].label

                                val intent = Intent(this@MainActivity, ResultActivity::class.java)
                                intent.putExtra(
                                    ResultActivity.EXTRA_IMAGE_URI,
                                    currentImageUri.toString()
                                )
                                intent.putExtra(
                                    ResultActivity.EXTRA_RESULT,
                                    "$resulText = $cancerPercentage%"
                                )
                                startActivity(intent)

                            } else {
                                showToast("Invalid result format")
                            }
                            binding.progressIndicator.visibility = View.GONE
                        }

                        override fun onError(error: String) {
                            showToast(error)
                            binding.progressIndicator.visibility = View.GONE
                        }
                    }
                )
                imageClassifierHelper.classifyStaticImage(currentImageUri!!)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(EXTRA_IMAGE_URI,currentImageUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentImageUri = savedInstanceState.getParcelable(EXTRA_IMAGE_URI)
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            binding.previewImageView.setImageURI(uri)
        } else {
            Log.d("MainActivity", "Image uri is null")
            showToast("image not selected")
        }

    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_IMAGE_URI = "image_uri"
    }
}