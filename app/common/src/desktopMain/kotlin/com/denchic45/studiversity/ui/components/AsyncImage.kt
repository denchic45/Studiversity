package com.denchic45.studiversity.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.unit.Density
import app.cash.sqldelight.db.use
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.xml.sax.InputSource
import java.io.BufferedInputStream
import java.io.File
import java.net.URL


@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    key: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    placeholderContent: @Composable (() -> Unit)? = null,
) {
    val image: T? by remember(key1 = key) {
        flow {
            emit(null)
            emit(load())
        }.catch { it.printStackTrace() }
    }.collectAsState(null, Dispatchers.IO)

    Box(modifier) {
        if (image != null) {
            Image(
                painter = painterFor(image!!),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        } else if (placeholderContent != null) {
            placeholderContent()
        }
    }
}


fun loadFileImageBitmap(path: String): ImageBitmap =
    File(path).inputStream().use(::loadImageBitmap)

fun loadBufferedInputStream(path: String): BufferedInputStream =
    File(path).inputStream().buffered()

fun loadImageBitmap(url: String): ImageBitmap =
    URL(url).openStream().buffered().use { loadImageBitmap(it) }

fun loadSvgPainter(url: String, density: Density): Painter =
    URL(url).openStream().buffered().use { loadSvgPainter(it, density) }

fun loadXmlImageVector(url: String, density: Density): ImageVector =
    URL(url).openStream().buffered().use { loadXmlImageVector(InputSource(it), density) }