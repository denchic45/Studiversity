package com.denchic45.studiversity.ui.coursework.submissiondetails

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.denchic45.studiversity.domain.resource.onLoading
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.attachment.AttachmentListItem
import com.denchic45.studiversity.ui.component.HeaderItemUI
import com.denchic45.studiversity.ui.coursework.SubmissionHeaderContent
import com.denchic45.studiversity.ui.coursework.SubmissionUiState
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.ui.search.UserListItem
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.util.AttachmentViewer
import com.denchic45.studiversity.util.collectWithLifecycle
import com.denchic45.studiversity.util.findActivity
import com.denchic45.studiversity.util.rememberImeState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionDetailsScreen(
    component: SubmissionDetailsComponent,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val attachmentViewer by lazy {
        AttachmentViewer(context.findActivity()) {
            Toast.makeText(
                context,
                "Невозможно открыть файл на данном устройстве",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

//    val uiStateResource by component.submissionState.collectAsState()
//    val allowGrade by component.allowGradeSubmission.collectAsState()
    val uiStateResource by component.uiState.collectAsState()

    component.openAttachment.collectWithLifecycle {
        attachmentViewer.openAttachment(it)
    }

    uiStateResource.onSuccess { uiState ->
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
        ) {
            val imeState by rememberImeState()
            Column(Modifier.let { if (imeState) it.padding(bottom = 220.dp) else it }) {
                SubmissionHeaderContent(uiState.first)
                UserListItem(
                    item = with(uiState.first.author) {
                        UserItem(id, firstName, surname, avatarUrl)
                    },
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.normal)
                )
                SubmissionDetailsContent(uiState.first, component::onAttachmentClick)
                Spacer(Modifier.height(MaterialTheme.spacing.normal))
                if (uiState.second)
                    SubmissionGradeContent(
                        uiState = uiState,
                        onGrade = component::onGrade,
                        onCancel = component::onGradeCancel
                    )
            }
        }
    }.onLoading {
        AlertDialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun SubmissionGradeContent(
    uiState: Pair<SubmissionUiState, Boolean>,
    onGrade: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        Modifier.padding(MaterialTheme.spacing.normal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        uiState.first.grade?.let {
            Button(onClick = onCancel) {
                Text("Отменить оценку")
            }
        } ?: run {
            var typedGrade by remember { mutableStateOf("") }
            OutlinedTextField(
                value = typedGrade,
                onValueChange = { value ->
                    if (value.isDigitsOnly() && value.toInt() in 1..5)
                        typedGrade = value
                },
                trailingIcon = { Text("/5") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.normal))
            FilledTonalButton(
                onClick = { onGrade(typedGrade.toInt()) },
                enabled = typedGrade.isNotEmpty()
            ) {
                Text("Оценить")
            }
        }
    }
}

@Composable
fun SubmissionDetailsContent(
    uiState: SubmissionUiState,
    onAttachmentClick: (AttachmentItem) -> Unit,
    onAttachmentRemove: ((attachmentId: UUID) -> Unit)? = null,
) {
    Column {
        HeaderItemUI(name = "Прикрепленные файлы")
        if (uiState.attachments.isNotEmpty()) {
            LazyRow(Modifier) {
                items(uiState.attachments, key = { it.attachmentId.toString() }) { item ->
                    Spacer(Modifier.width(MaterialTheme.spacing.normal))
                    AttachmentListItem(
                        item = item,
                        onClick = { onAttachmentClick(item) },
                        onRemove = onAttachmentRemove?.let { { it(item.attachmentId) } })
                }
            }
        } else {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(96.dp), contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.padding(MaterialTheme.spacing.normal),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberVectorPainter(image = Icons.Outlined.Attachment),
                        contentDescription = "empty attachments"
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.normal))
                    Text("Нет прикрепленных файлов")
                }
            }
        }
    }
}