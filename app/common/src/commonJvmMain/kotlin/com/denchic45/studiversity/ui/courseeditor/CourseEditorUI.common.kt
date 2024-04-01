package com.denchic45.studiversity.ui.courseeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun CourseEditorContent(
    state: EditingCourse,
    onNameChange: (String) -> Unit,
    onSubjectChoose: () -> Unit,
    onSubjectClose: () -> Unit,
) {
    Column {
        OutlinedTextField(
            value = state.name,
            onValueChange = { onNameChange(it) },
            singleLine = true,
            label = { Text(text = "Название") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.normal)
        )
        Spacer(Modifier.height(MaterialTheme.spacing.normal))
        ListItem(
            headlineContent = {
                val alpha = if (state.subject?.subjectName != null) 1f
                else ContentAlpha.disabled
                Text(
                    text = state.subject?.subjectName ?: "Выберите предмет",
                    modifier = Modifier.alpha(alpha = alpha)
                )
            },
            modifier = Modifier.clickable(onClick = onSubjectChoose),
//                leadingContent = { todo вернуть
//                    state.subject?.subjectIconUrl?.let {
//                        Icon(
//                            painter = rememberAsyncImagePainter(
//                                ImageRequest.Builder(PlatformContext.INSTANCE)
////                                    .decoderFactory(SvgDecoder.Factory()) // todo ВЕРНУТЬ
//                                    .data(it)
//                                    .build()
//                            ),
//                            contentDescription = "subject icon",
//                            modifier = Modifier.size(40.dp),
//                            tint = MaterialTheme.colorScheme.secondary
//                        )
//                    } ?: Icon(
////                        painter = painterResource(id = R.drawable.ic_subject), todo ВЕРНУТЬ
//                        Icons.Outlined.Subject,
//                        contentDescription = "subject icon"
//                    )
//                },
            trailingContent = {
                IconButton(onClick = onSubjectClose) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "remove subject"
                    )
                }
            }
        )
    }
}