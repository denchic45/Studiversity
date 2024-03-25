package com.denchic45.studiversity.ui.coursework

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.coursework.submissions.CourseWorkSubmissionsComponent
import com.denchic45.studiversity.ui.search.UserAvatarImage
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.submission.model.SubmissionByAuthor
import com.denchic45.stuiversity.api.submission.model.SubmissionState
import com.denchic45.stuiversity.util.toString


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CourseWorkSubmissionsScreen(component: CourseWorkSubmissionsComponent) {
    val submissionsResource by component.submissions.collectAsState()
    val slot by component.childSlot.subscribeAsState()

    val refreshing by component.refreshing.collectAsState()
    val refreshState = rememberPullRefreshState(refreshing, component::onRefresh)

    Box(modifier = Modifier.pullRefresh(refreshState)) {
        SubmissionsContent(submissionsResource, component)
        PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
    }

    slot.child?.let {
        SubmissionDetailsScreen(
            component = it.instance.component,
            onDismissRequest = component::onSubmissionClose
        )
    }
}

@Composable
private fun SubmissionsContent(
    submissionsResource: Resource<List<SubmissionByAuthor>>,
    component: CourseWorkSubmissionsComponent
) {
    submissionsResource.onSuccess {
        LazyColumn {
            items(it, key = { it.author.id }) { submissionByAuthor ->
                ListItem(
                    modifier = Modifier
                        .clickable { component.onStudentClick(submissionByAuthor.author.id) }
                        .padding(vertical = MaterialTheme.spacing.small),
                    leadingContent = {
                        UserAvatarImage(url = submissionByAuthor.author.avatarUrl)
                    },
                    headlineContent = {
                        Row {
                            Text(
                                submissionByAuthor.author.fullName,
                                modifier = Modifier.weight(1f),
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    trailingContent = {
                        submissionByAuthor.submission?.let { submission ->
                            val updatedAt = submission.updatedAt?.toString("dd MMM")
                            val text = when (submission.state) {
                                SubmissionState.CREATED -> "Не сдано"

                                SubmissionState.SUBMITTED -> "Сдано $updatedAt"
                                SubmissionState.CANCELED_BY_AUTHOR -> "Отменено $updatedAt"
                            }
                            submission.grade?.let {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = it.value.toString(),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(text = text)
                                }
                            } ?: Text(text = text, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                )
            }
        }
    }
}