package com.denchic45.kts.ui.subjecticons

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.denchic45.kts.ui.ResourceContent

@Composable
fun SubjectIconsDialog(component: SubjectIconsComponent) {
    val iconUrlsResource by component.iconUrls.collectAsState()
    ResourceContent(resource = iconUrlsResource) {
        SubjectIconsContent(iconUrls = it)
    }
}

@Composable
fun SubjectIconsContent(iconUrls: List<String>) {
    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(iconUrls, key = { it }) { url ->
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "subject icon",
                modifier = Modifier.fillMaxSize(),
                loading = { CircularProgressIndicator() },
            )
        }
    }
}
