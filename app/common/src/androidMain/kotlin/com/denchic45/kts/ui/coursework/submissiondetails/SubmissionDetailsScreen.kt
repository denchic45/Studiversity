package com.denchic45.kts.ui.coursework.submissiondetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.coursework.SubmissionHeaderContent
import com.denchic45.kts.ui.coursework.SubmissionUiState
import com.denchic45.kts.ui.coursework.details.AttachmentItemUI
import com.denchic45.kts.ui.theme.spacing
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionDetailsScreen(
    component: SubmissionDetailsComponent,
    onDismissRequest: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val uiStateResource by component.uiState.collectAsState()
    uiStateResource.onSuccess { uiState ->
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
//            SideEffect {
//                coroutineScope.launch { sheetState.show() }
//            }
            SubmissionHeaderContent(uiState)
            SubmissionDetailsContent(uiState)

            Spacer(Modifier.height(MaterialTheme.spacing.normal))
        }
    }
}

@Composable
fun SubmissionDetailsContent(
    uiState: SubmissionUiState,
    onAttachmentRemove: ((attachmentId: UUID) -> Unit)? = null
) {
    Column {
        if (uiState.attachments.isNotEmpty()) {
            HeaderItemUI(name = "Прикрепления")
            LazyRow(Modifier) {
                items(uiState.attachments, key = { it.attachmentId?.toString() ?: "" }) { item ->
                    Spacer(Modifier.width(MaterialTheme.spacing.normal))
                    AttachmentItemUI(item = item) {
                        onAttachmentRemove?.let { it(item.attachmentId!!) }
                    }
                }
            }
        }
    }
}