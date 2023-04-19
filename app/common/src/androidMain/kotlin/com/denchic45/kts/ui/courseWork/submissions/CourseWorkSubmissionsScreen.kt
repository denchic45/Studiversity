package com.denchic45.kts.ui.courseWork.submissions

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import coil.compose.AsyncImage
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.courseWork.submissiondetails.SubmissionDetailsScreen
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CourseWorkSubmissionsScreen(component: CourseWorkSubmissionsComponent) {
    val submissionsResource by component.submissions.collectAsState()
    val slot by component.childOverlay.subscribeAsState()

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetContent = {
            slot.overlay?.let {
                coroutineScope.launch { sheetState.show() }
                SubmissionDetailsScreen(it.instance.component)
            }
        },
        sheetState = sheetState
    ) {
        submissionsResource.onSuccess { submissions ->
            LazyColumn {
                items(submissions, key = { it.id }) { submission ->
                    ListItem(
                        leadingContent = {
                            AsyncImage(submission, "Submission")
                        },
                        headlineText = {
                            Text(submission.author.fullName)
                        },
                        trailingContent = {
                            val updatedAt = submission.updatedAt?.toString("dd MMM")
                            Text(
                                submission.grade?.let {
                                    "Оценено: $it"
                                } ?: when (submission.state) {
                                    SubmissionState.NEW,
                                    SubmissionState.CREATED -> "Не сдано"

                                    SubmissionState.SUBMITTED -> "Отправлено $updatedAt"
                                    SubmissionState.CANCELED_BY_AUTHOR -> "Отменено автором $updatedAt"
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}