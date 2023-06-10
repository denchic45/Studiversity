package com.denchic45.studiversity.data.service

import com.denchic45.studiversity.data.domain.model.FileState
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
actual class DownloadsService @javax.inject.Inject actual constructor() {
    actual fun download(attachmentId: UUID): Flow<FileState> {
        TODO("Not yet implemented")
    }

    actual fun getDownloading(attachmentId: UUID): Flow<FileState> {
        TODO("Not yet implemented")
    }

    actual fun cancel(attachmentId: UUID) {
    }
}