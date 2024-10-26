package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.firebase.components.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val  FILEMAME_Format = "yyyymmdd_HHmmss"
private val timestamp: String = SimpleDateFormat(FILEMAME_Format,Locale.US).format(Date())

fun getImageUri(context : Context):Uri{
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,"$timestamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH,"Pictures/MyCamera")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

    }
    return uri ?: getImageUri(context)
}

private fun getImageUriPreQ(context: Context):Uri{
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val ImageFile = File(dir,"/camera/$timestamp.jpg")
    if (ImageFile.parentFile?.exists() == false) ImageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        ImageFile
    )
}

fun createCustomTempFile(context: Context):File{
    val filesDir = context.externalCacheDir
    return File.createTempFile("tempImage",".jpg",filesDir)
}