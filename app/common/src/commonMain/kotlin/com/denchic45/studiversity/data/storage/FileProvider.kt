package com.denchic45.studiversity.data.storage

import com.eygraber.uri.Uri

expect class FileProvider {
    fun getBytes(uri:Uri):ByteArray

    fun getName(uri: Uri):String
}