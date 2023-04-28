package com.denchic45.kts.data.storage

import com.eygraber.uri.Uri

expect class FileProvider {
    fun getBytes(uri:Uri):ByteArray

    fun getName(uri: Uri):String
}