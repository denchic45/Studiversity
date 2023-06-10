package com.denchic45.studiversity.ui.search

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse

@Composable
fun SubjectChooserScreen(component: SubjectChooserComponent) {
    SearchScreen(
        component = component,
        keyItem = { it.id },
        itemContent = {
            SubjectListItem(it)
        })
}

@Composable
fun SubjectListItem(item: SubjectResponse, trailingContent: @Composable (() -> Unit)? = null) {
    ListItem(
        headlineContent = { Text(item.name) },
        leadingContent = {
            Icon(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .decoderFactory(SvgDecoder.Factory())
                        .data(item.iconUrl)
                        .build()
                ),
                contentDescription = "subject icon",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        trailingContent = trailingContent
    )
}