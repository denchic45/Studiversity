package com.denchic45.studiversity.feature.course.subject

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import java.io.InputStream

class SubjectIconStorage(private val fileSystem: FileSystem) {
    private val subjectIconsDir = "data/subject icons".toPath()

    fun getStreamByName(name: String): InputStream {
        return fileSystem.source(subjectIconsDir / name).buffer().inputStream()
    }
}