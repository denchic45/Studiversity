package com.denchic45.kts.ui.periodeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun LessonDetailsEditorScreen(component: LessonDetailsEditorComponent) {
    val details = remember { component.details }
    LessonDetailsEditorContent(details, onCourseChoose = component::onCourseChoose)
}

@Composable
fun LessonDetailsEditorContent(details: EditingPeriodDetails.Lesson, onCourseChoose: () -> Unit) {
    Column {
        Box(modifier = Modifier.clickable(onClick = onCourseChoose)) {
            details.course?.let { course ->
                ListItem(
                    headlineContent = {
                        Text(text = course.subject?.name ?: course.name)
                    },
                    leadingContent = {
                        Icon(
                            painter = course.subject?.iconUrl?.let { url ->
                                rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .decoderFactory(SvgDecoder.Factory())
                                        .data(url)
                                        .build()
                                )
                            } ?: rememberVectorPainter(Icons.Outlined.School),
                            contentDescription = "icon course",
                            modifier = Modifier.size(40.dp)
                        )
                    })
            } ?: ListItem(
                headlineContent = { Text(text = "Выберите курс") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.School,
                        contentDescription = "icon select course",
                        modifier = Modifier.size(40.dp)
                    )
                }
            )
        }
    }
}