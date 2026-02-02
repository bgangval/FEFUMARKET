package com.example.fefumarket.data.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

// Утилита для работы с файлами: получение реального пути к файлу из Uri
object FileUtils {

    // Возвращает путь к файлу на устройстве по Uri, или null, если не найден
    fun getPath(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)

        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val path = columnIndex?.let { cursor.getString(it) }

        cursor?.close()

        return path
    }
}