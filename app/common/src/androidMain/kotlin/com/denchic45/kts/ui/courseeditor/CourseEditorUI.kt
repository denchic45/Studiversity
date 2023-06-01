package com.denchic45.kts.ui.courseeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.kts.R
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.resourceOf
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.appbar2.ActionMenuItem2
import com.denchic45.kts.ui.appbar2.AppBarContent
import com.denchic45.kts.ui.appbar2.LocalAppBarState
import com.denchic45.kts.ui.chooser.SubjectChooserScreen
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.kts.ui.uiIconOf
import com.denchic45.kts.ui.uiTextOf
import java.util.UUID


@Composable
fun CourseEditorScreen(component: CourseEditorComponent) {
    val resource by component.viewState.collectAsState()
    val navigation by component.childOverlay.subscribeAsState()
    val saveEnabled by component.saveEnabled.collectAsState()

    val appBarState = LocalAppBarState.current

    component.lifecycle.doOnStart {
        appBarState.content = AppBarContent(
            title = uiTextOf(if (component.isNew) "Новый курс" else "Редактировать курс"),
            actionItems = listOf(ActionMenuItem2(
                uiIconOf(Icons.Default.Done),
                uiTextOf(if (component.isNew) "Новый курс" else "Редактировать курс"),
            saveEnabled,
                component::onSaveClick
                ))
        )


//        appBarState.set(
//            AppBarState(
//                title = uiTextOf(if (component.isNew) "Новый курс" else "Редактировать курс"),
//                actionsUI = {
//                    IconButton(enabled = saveEnabled, onClick = component::onSaveClick) {
//                        Icon(
//                            imageVector = Icons.Default.Done,
//                            contentDescription = "done"
//                        )
//                    }
//                })
//        )
    }

    when (val child = navigation.overlay?.instance) {
        is CourseEditorComponent.DialogChild.SubjectChooser -> {
            SubjectChooserScreen(
                component = child.component,
            )
        }

        null -> CourseEditorContent(
            uiStateResource = resource,
            onNameType = component::onCourseNameType,
            onSubjectChoose = component::onSubjectChoose,
            onSubjectClose = component::onSubjectClose
        )
    }
}

@Composable
fun CourseEditorContent(
    uiStateResource: Resource<CourseEditorComponent.EditingCourse>,
    onNameType: (String) -> Unit,
    onSubjectChoose: () -> Unit,
    onSubjectClose: () -> Unit,
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
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .decoderFactory(SvgDecoder.Factory())
                                        .data(it)
                                        .build()
                                ),
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