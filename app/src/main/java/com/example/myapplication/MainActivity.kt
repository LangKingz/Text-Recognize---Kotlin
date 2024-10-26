package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private var currentImageUri: Uri? = null

//    permintaaan Izin
    private val requestPermisionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if (isGranted){
            currentImageUri = getImageUri(this)
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSIONS
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!allPermissionsGranted()){
            requestPermisionLauncher.launch(REQUIRED_PERMISSIONS)
        }

        binding.galleryButton.setOnClickListener{
            startGalllery()
        }
        binding.cameraButton.setOnClickListener{
            currentImageUri = getImageUri(this)
            launcherKameraIntent.launch(currentImageUri!!)
        }
        binding.cameraXButton.setOnClickListener{
            val intent = Intent(this,CameraActivity::class.java)
            launcherIntentKameraX.launch(intent)
        }
        binding.analyzeButton.setOnClickListener{
            val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val inputImage = InputImage.fromFilePath(this,currentImageUri!!)
            textRecognizer.process(inputImage)
                .addOnSuccessListener { Vissiontext ->
                    val detected :String = Vissiontext.text
                    if(detected.isNotBlank()){
                        binding.progressIndicator.visibility = android.view.View.VISIBLE
                        val intent = Intent(this,ResultActivity::class.java)
                        intent.putExtra(ResultActivity.EXTRA_RESULT,detected)
                        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI,currentImageUri.toString())
                        startActivity(intent)
                    }else{
                        Toast.makeText(this,"No text detected",Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{
                    binding.progressIndicator.visibility = android.view.View.GONE
                    Toast.makeText(this,"Failed to detect text",Toast.LENGTH_SHORT).show()
                }

        }

    }

//   Fungsi Galeri
    private fun startGalllery(){
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){ uri :Uri? ->
       if (uri!=null){
//           Menampilkan gambar yang dipilih ke ImageView
            currentImageUri = uri
            binding.previewImageView.setImageURI(uri)
       }else{
//           Log jika gambar tidak dipilih
           Log.d("MainActivity","Image not selected")
           Toast.makeText(this,"Image not selected",Toast.LENGTH_SHORT).show()
       }
    }

//    fungsi Launcher Kamera
    private val launcherKameraIntent = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){
        isSuccess ->
        if (isSuccess){
            binding.previewImageView.setImageURI(currentImageUri)
        }else{
            Log.d("MainActivity","Image not selected")
            Toast.makeText(this,"Image not selected",Toast.LENGTH_SHORT).show()
        }
   }

//    kamera x
    private val launcherIntentKameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == CameraActivity.CAMERAX_RESULT){
            val uri = Uri.parse(it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE))
            currentImageUri = uri
            binding.previewImageView.setImageURI(uri)
        }
    }



    companion object {
        private const val REQUIRED_PERMISSIONS = android.Manifest.permission.CAMERA
    }
}