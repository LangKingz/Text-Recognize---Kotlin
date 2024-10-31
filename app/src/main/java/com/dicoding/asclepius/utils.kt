package com.dicoding.asclepius

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private const val FILEFORMAT = "yyyymmdd_HHmmss"
private val timestamp :String = SimpleDateFormat(FILEFORMAT, Locale.US).format(Date())

fun getImageUri(context : Context):Uri{
    var uri: Uri? = null

//    cek apakah version sdk nya sesuai persyaratan
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,"$timestamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH,"APLIKASI SUBMISSION/CAMERA")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

    }
    return uri?: getImageUri(context)
}


