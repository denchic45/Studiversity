package com.denchic45.kts.ui.courseeditor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.R
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.resourceOf
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.chooser.SubjectChooserScreen
import com.denchic45.kts.ui.theme.spacing
import java.util.UUID


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CourseEditorScreen(component: CourseEditorComponent, appBarInteractor: AppBarInteractor) {
    val resource by component.viewState.collectAsState()
    val navigation by component.childOverlay.subscribeAsState()

    AnimatedContent(targetState = navigation.overlay?.instance) {
        when (it) {
            is CourseEditorComponent.DialogChild.SubjectChooser -> {
                SubjectChooserScreen(component = it.component, appBarInteractor = appBarInteractor)
            }

            null -> CourseEditorContent(
                uiStateResource = resource,
                onNameType = component::onCourseNameType,
                onSubjectChoose = component::onSubjectChoose,
                onSubjectClose = component::onSubjectClose
            )
        }
    }
}

@Composable
fun CourseEditorContent(
    uiStateResource: Resource<CourseEditorComponent.EditingCourse>,
    onNameType: (String) -> Unit,
    onSubjectChoose: () -> Unit,
    onSubjectClose: () -> Unit
) {
    Surface {
        uiStateResource.onSuccess { uiState ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(vertical = MaterialTheme.spacing.medium)
            ) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { onNameType(it) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.normal)
                )
                Spacer(Modifier.height(MaterialTheme.spacing.normal))
                ListItem(
                    headlineContent = {
                        val alpha = if (uiState.subject?.subjectName != null) 1f
                        else ContentAlpha.disabled
                        Text(
                            text = uiState.subject?.subjectName ?: "Выберите предмет",
                            modifier = Modifier.alpha(alpha = alpha)
                        )
                    },
                    modifier = Modifier.clickable(onClick = onSubjectChoose),
                    leadingContent = {
                        uiState.subject?.subjectIconUrl?.let {
                            Icon(
                                painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .data(it)
                                    .build()),
                                contentDescription = "subject icon",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        } ?: Icon(
                            painter = painterResource(id = R.drawable.ic_subject),
                            contentDescription = "subject icon"
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { onSubjectClose() }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "remove subject"
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun CourseEditorPreview() {
    CourseEditorContent(
        uiStateResource = resourceOf(CourseEditorComponent.EditingCourse().apply {
            name = "Course name"
            subject = CourseEditorComponent.SelectedSubject(
                subjectId = UUID.randomUUID(),
                subjectName = "Subject name",
                subjectIconUrl = ""
            )
        }),
        onNameType = {},
        onSubjectChoose = {},
        onSubjectClose = {})
}