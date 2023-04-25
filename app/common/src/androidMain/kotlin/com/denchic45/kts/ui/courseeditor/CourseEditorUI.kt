package com.denchic45.kts.ui.courseeditor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.R
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.subjectchooser.SubjectChooserScreen
import com.denchic45.kts.ui.theme.spacing


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CourseEditorScreen(component: CourseEditorComponent) {
    val resource by component.uiState.collectAsState()
    val navigation by component.childOverlay.subscribeAsState()

    AnimatedContent(targetState = navigation.overlay?.instance) {
        when (it) {
            is CourseEditorComponent.DialogChild.SubjectChooser -> {
                SubjectChooserScreen(component = it.component)
            }

            null -> CourseEditorContent(
                uiStateResource = resource,
                onNameType = component::onCourseNameType,
                onSubjectChoose = component::onSubjectChoose
            )
        }
    }
}

@Composable
fun CourseEditorContent(
    uiStateResource: Resource<CourseEditorComponent.EditingCourse>,
    onNameType: (String) -> Unit,
    onSubjectChoose: () -> Unit
) {
    uiStateResource.onSuccess { uiState ->
        Column(Modifier.padding(MaterialTheme.spacing.medium)) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { onNameType(it) }
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { onSubjectChoose() }
                    .padding(
                        horizontal = MaterialTheme.spacing.normal,
                        vertical = MaterialTheme.spacing.small
                    )
            ) {
                uiState.subject?.subjectIconUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "subject icon"
                    )
                } ?: Icon(
                    painter = painterResource(id = R.drawable.ic_subject),
                    contentDescription = "sujbect icon"
                )

                val alpha = if (uiState.subject?.subjectName != null) 1f else ContentAlpha.disabled

                Text(
                    text = uiState.subject?.subjectName ?: "Выберите предмет",
                    modifier = Modifier
                        .padding(
                            start = MaterialTheme.spacing.normal,
                            end = MaterialTheme.spacing.medium
                        )
                        .alpha(alpha = alpha),
                    color = Color.LightGray.copy(LocalContentAlpha.current)
                )
            }
        }
    }
}