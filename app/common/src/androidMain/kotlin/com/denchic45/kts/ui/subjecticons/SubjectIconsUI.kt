package com.denchic45.kts.ui.subjecticons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.denchic45.kts.ui.CircularLoadingBox
import com.denchic45.kts.ui.ResourceContent
import com.denchic45.kts.ui.theme.spacing

@Composable
fun SubjectIconsDialog(component: SubjectIconsComponent) {
    val iconUrlsResource by component.iconUrls.collectAsState()
    AlertDialog(
        onDismissRequest = { component.onDismiss() },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = component::onDismiss) {
                Text(text = "Отмена")
            }
        },
        title = { Text(text = "Выбор иконки предмета") },
        text = {
            ResourceContent(
                resource = iconUrlsResource,
                onLoading = {
                    CircularLoadingBox(
                        Modifier
                            .fillMaxWidth()
                            .height(332.dp)
                    )
                }) {
                SubjectIconsContent(iconUrls = it, component::onSelectImage)
            }
        }
    )
}

@Composable
fun SubjectIconsContent(iconUrls: List<String>, onIconClick: (String) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(iconUrls, key = { it }) { url ->
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "subject icon",
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onIconClick(url) }
                    .padding(MaterialTheme.spacing.normal),
                loading = { CircularProgressIndicator(Modifier.padding(MaterialTheme.spacing.normal)) },
            )
        }
    }
}
