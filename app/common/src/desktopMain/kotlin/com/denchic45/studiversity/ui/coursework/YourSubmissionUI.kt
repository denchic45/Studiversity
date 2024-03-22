package com.denchic45.studiversity.ui.coursework

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.BlockContent
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.coursework.yourSubmission.YourSubmissionComponent
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.stuiversity.util.UUIDWrapper

@Composable
fun YourSubmissionBlock(component: YourSubmissionComponent) {
    val submissionResource by component.submission.collectAsState()
    val attachmentsComponent by component.attachmentsComponentResource.collectAsState()

    ResourceContent(submissionResource) { submission ->
        ResourceContent(attachmentsComponent) { attachmentsComponent ->
            val attachmentsIsEmpty by attachmentsComponent.isEmpty().collectAsState()
            YourSubmissionContent(
                submission = submission,
                attachmentContent = {
                    val attachments by attachmentsComponent.attachments.collectAsState()
                    attachments.onSuccess {
                        SubmissionAttachments(
                            attachments = it,
                            onAttachmentAdd = { TODO() },
                            onAttachmentClick = attachmentsComponent::onAttachmentClick,
                            onAttachmentRemove = {

                            }
                        )
                    }
                },
                attachmentsIsEmpty = attachmentsIsEmpty.takeValueIfSuccess() ?: true,
                onAttachmentAdd = { TODO() },
                onSubmit = component::onSubmit,
                onCancel = component::onCancel
            )
        }
    }
}


@Composable
fun YourSubmissionContent(
    submission: SubmissionUiState,
    attachmentContent: @Composable () -> Unit,
    attachmentsIsEmpty: Boolean,
    onAttachmentAdd: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {
    BlockContent {
        Row(Modifier.height(64.dp)) {
            Text(submission.title)
            if (submission.late) {
                Spacer(Modifier.weight(1f))
                Text("'Пропущен срок сдачи", color = MaterialTheme.colorScheme.error)
            }
        }
        attachmentContent()
        when (submission.state) {
            SubmissionState.CREATED,
            SubmissionState.CANCELED_BY_AUTHOR -> {
                OutlinedButton(onClick = onAttachmentAdd, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.Add, null)
                    Spacer(Modifier.width(MaterialTheme.spacing.small))
                    Text("Отправить")
                }
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                if (!attachmentsIsEmpty) {
                    Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth()) {
                        Text("Сдать")
                    }
                }
            }

            SubmissionState.SUBMITTED -> FilledTonalButton(
                onClick = { onCancel() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(text = "Отменить отправку")
            }
        }
    }
}

@Composable
fun SubmissionAttachments(
    attachments: List<AttachmentItem>,
    onAttachmentAdd: () -> Unit,
    onAttachmentClick: (AttachmentItem) -> Unit,
    onAttachmentRemove: (attachmentId: UUIDWrapper.UUID) -> Unit,
) {

}