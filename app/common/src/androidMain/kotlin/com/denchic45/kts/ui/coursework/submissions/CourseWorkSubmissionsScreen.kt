package com.denchic45.kts.ui.coursework.submissions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.coursework.submissiondetails.SubmissionDetailsScreen
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.stuiversity.util.toString


@Composable
fun CourseWorkSubmissionsScreen(component: CourseWorkSubmissionsComponent) {
    val submissionsResource by component.submissions.collectAsState()
    val slot by component.childOverlay.subscribeAsState()

    submissionsResource.onSuccess { submissions ->
        LazyColumn {
            items(submissions, key = { it.id }) { submission ->
                ListItem(
                    modifier = Modifier.clickable {
                        component.onSubmissionClick(submission.id)
                    },
                    leadingContent = {
                        AsyncImage(submission, "Submission")
                    },
                    headlineContent = {
                        Text(submission.author.fullName)
                    },
                    trailingContent = {
                        val updatedAt = submission.updatedAt?.toString("dd MMM")
                        Text(
                            submission.grade?.let {
                                "Оценено: $it"
                            } ?: when (submission.state) {
                                SubmissionState.NEW,
                                SubmissionState.CREATED,
                                -> "Не сдано"

                                SubmissionState.SUBMITTED -> "Отправлено $updatedAt"
                                SubmissionState.CANCELED_BY_AUTHOR -> "Отменено автором $updatedAt"
                            }
                        )
                    }
                )
            }
        }
    }
    slot.overlay?.let {
        SubmissionDetailsScreen(
            component = it.instance.component,
            onDismissRequest = component::onSubmissionClose
        )
    }
}