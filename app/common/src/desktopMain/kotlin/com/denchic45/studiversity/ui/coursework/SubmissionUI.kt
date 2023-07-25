package com.denchic45.studiversity.ui.coursework

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.model.AttachmentItem
import java.util.UUID

@Composable
fun SubmissionPanel(
    resource: Resource<SubmissionUiState>,
    onAttachmentAdd: () -> Unit,
    onAttachmentClick: (AttachmentItem) -> Unit,
    onAttachmentRemove: (attachmentId: UUID) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {

}