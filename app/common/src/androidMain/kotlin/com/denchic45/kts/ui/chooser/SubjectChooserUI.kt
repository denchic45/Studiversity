package com.denchic45.kts.ui.chooser

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
import com.denchic45.kts.ui.appbar.AppBarInteractor

@Composable
fun SubjectChooserScreen(component: SubjectSearchComponent, appBarInteractor: AppBarInteractor) {
    SearchScreen(
        component = component,
        appBarInteractor = appBarInteractor,
        keyItem = { it.id }, itemContent = {
            ListItem(
                headlineContent = { Text(it.name) },
                leadingContent = {
                    Icon(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .decoderFactory(SvgDecoder.Factory())
                                .data(it.iconUrl)
                                .build()
                        ),
                        contentDescription = "subject icon",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            )
        })
}