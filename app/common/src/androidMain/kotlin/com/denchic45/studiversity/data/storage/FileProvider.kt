package com.denchic45.studiversity.data.storage

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.OpenableColumns
import com.eygraber.uri.Uri
import com.eygraber.uri.toAndroidUri
import me.tatarka.inject.annotations.Inject

actual class FileProvider(private val contentResolver: ContentResolver) {
    @SuppressLint("Recycle")
    actual fun getBytes(uri: Uri): ByteArray {
        return contentResolver.openInputStream(uri.toAndroidUri())!!.use { it.readBytes() }
    }

    actual fun getName(uri: Uri): String {
        return contentResolver.query(uri.toAndroidUri(), null, null, null, null)!!.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }
}