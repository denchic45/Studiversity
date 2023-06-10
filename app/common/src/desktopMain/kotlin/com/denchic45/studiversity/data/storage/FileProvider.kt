package com.denchic45.studiversity.data.storage

import com.eygraber.uri.Uri
import com.eygraber.uri.toURI
import me.tatarka.inject.annotations.Inject
import kotlin.io.path.name
import kotlin.io.path.toPath

@Inject
actual class FileProvider {
    actual fun getBytes(uri: Uri): ByteArray {
     return  uri.toURI().toPath().toFile().readBytes()
    }

    actual fun getName(uri: Uri): String {
        return  uri.toURI().toPath().name
    }

}