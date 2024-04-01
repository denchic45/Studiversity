package com.denchic45.studiversity.ui.subject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.api.course.subject.model.SubjectResponse

@Composable
fun SubjectListItem(
    item: SubjectResponse,
    onClick: () -> Unit = {},
    trailingContent: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(item.name) },
//        leadingContent = {
//            Icon(
//                painter = rememberAsyncImagePainter(
//                    ImageRequest.Builder(LocalContext.current)
//                        .decoderFactory(SvgDecoder.Factory())
//                        .data(item.iconUrl)
//                        .build()
//                ),
//                contentDescription = "subject icon",
//                modifier = Modifier.size(40.dp),
//                tint = MaterialTheme.colorScheme.secondary
//            )
//        },
        trailingContent = trailingContent,
        modifier = modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    )
}