package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.dicoding.asclepius.data.database.History
import com.dicoding.asclepius.data.database.roomHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ImageClassifierHelper(
    var threshold : Float = 0.1f,
    var maxResult: Int =2,
    val modelName :String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener : ClassifierListener?
) {
    private var imageClassifier : ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResult)

        val baseoptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseoptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        }catch (e: Exception){
            classifierListener?.onError(e.message.toString())
            Log.d(TAG, "setupImageClassifier: ${e.message}")
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

        var bestResults: List<Classifications>? = null
        var highestConfidence = 0f

        val rotations = listOf(0, 90, 180, 270)

        for (rotation in rotations) {
            val rotatedBitmap = rotateBitmap(bitmap, rotation)
            val results = classifyBitmap(rotatedBitmap)

            val topResult = results?.maxByOrNull { it.categories[0].score }
            if (topResult != null && topResult.categories[0].score > highestConfidence) {
                highestConfidence = topResult.categories[0].score
                bestResults = results
            }
        }

        classifierListener?.onResult(bestResults)
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    private fun classifyBitmap(bitmap: Bitmap): List<Classifications>? {
        if (imageClassifier == null) {
            setupImageClassifier()
        }

        val imageProcess = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.FLOAT32))
            .build()

        val tensorImage = imageProcess.process(TensorImage.fromBitmap(bitmap))

        return imageClassifier?.classify(tensorImage)
    }

    interface ClassifierListener{
        fun onResult(result: List<Classifications>?)
        fun onError(error: String)
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }

}
