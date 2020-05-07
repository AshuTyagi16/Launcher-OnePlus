package com.sasuke.launcheroneplus.util

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class StorageUtils(context: Context) {

    private val dir = context.getExternalFilesDir("app_icon")

    @WorkerThread
    suspend fun saveBitmapToFile(bitmap: Bitmap, label: String): String? {
        try {
            val path = "$dir${File.separator}$label"
            FileOutputStream(File(path)).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            return path
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

}