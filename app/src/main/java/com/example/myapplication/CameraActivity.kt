package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.core.Preview
import androidx.camera.lifecycle.LifecycleCamera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityCameraBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var barcodeScanner: BarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun startCamera(){
        val option = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        barcodeScanner = BarcodeScanning.getClient(option)
        var firstCall = true
        val analyzer = MlKitAnalyzer(
            listOf(barcodeScanner),
            COORDINATE_SYSTEM_VIEW_REFERENCED,
            ContextCompat.getMainExecutor(this)
        ){  result: MlKitAnalyzer.Result ->
            if(firstCall){
                val barcodeResult = result.getValue(barcodeScanner)
                if((barcodeResult != null) &&
                    (barcodeResult.size != 0) &&
                    (barcodeResult.first() != null))
                {
                    firstCall = false
                    val barcode = barcodeResult.first()
                    val alertDialog = AlertDialog.Builder(this)
                        .setMessage(barcode.rawValue)
                        .setPositiveButton(
                            "Buka"
                        ) { _, _ ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(barcode.rawValue))
                            startActivity(intent)
                        }
                        .setNegativeButton("Scan lagi") { _, _ ->
                            firstCall = true
                        }
                        .setCancelable(false)
                        .create()
                    alertDialog.show()
                }
            }
        }

        val cameraController = LifecycleCameraController(baseContext)
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(this),
            analyzer
        )
        cameraController.bindToLifecycle(this)
        binding.viewFinder.controller = cameraController
    }

    private fun hideSystemUI(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsetsCompat.Type.systemBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    companion object{
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "extra_camerax_image"
        const val CAMERAX_RESULT = 200
    }
}