package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.service.DownloadsService
import com.denchic45.studiversity.domain.model.FileState
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class DownloadFileUseCase(
    private val downloadsService: DownloadsService
) {

    operator fun invoke(attachmentId: UUID): Flow<FileState> {
        return downloadsService.download(attachmentId)
    }
}