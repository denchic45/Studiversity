package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.data.service.DownloadsService
import kotlinx.coroutines.flow.Flow
import java.util.*

class DownloadFileUseCase(
    private val downloadsService: DownloadsService
) {

    operator fun invoke(attachmentId: UUID): Flow<FileState> {
        return downloadsService.download(attachmentId)
    }
}