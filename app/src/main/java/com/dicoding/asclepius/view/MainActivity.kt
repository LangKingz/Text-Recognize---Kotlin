package com.dicoding.asclepius.view


import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.getImageUri
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications


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
                                val noCancerProbability = result[0].categories[1].score
                                // Hitung persentase
                                val cancerPercentage = (cancerProbability * 100).toInt()
                                val noCancerPercentage = (noCancerProbability * 100).toInt()


                                // Kirim hasil ke ResultActivity
                                if (cancerPercentage > noCancerPercentage) {
                                    val intent =
                                        Intent(this@MainActivity, ResultActivity::class.java)
                                    intent.putExtra(
                                        ResultActivity.EXTRA_IMAGE_URI,
                                        currentImageUri.toString()
                                    )
                                    intent.putExtra(
                                        ResultActivity.EXTRA_RESULT,
                                        "Cancer : $cancerPercentage%"
                                    )
                                    startActivity(intent)
                                } else {
                                    val intent =
                                        Intent(this@MainActivity, ResultActivity::class.java)
                                    intent.putExtra(
                                        ResultActivity.EXTRA_IMAGE_URI,
                                        currentImageUri.toString()
                                    )
                                    intent.putExtra(
                                        ResultActivity.EXTRA_RESULT,
                                        "No Cancer : $noCancerPercentage%"
                                    )
                                    startActivity(intent)
                                }
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
}