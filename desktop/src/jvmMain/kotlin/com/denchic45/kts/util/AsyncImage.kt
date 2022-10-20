package com.denchic45.kts.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.unit.Density
import com.squareup.sqldelight.db.use
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.xml.sax.InputSource
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.net.URL


@Composable
fun <T> AsyncImageOriginal(
    url: String,
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val image: T? by produceState<T?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                load()
            } catch (e: IOException) {
                // instead of printing to console, you can also write this to log,
                // or show some error placeholder
                e.printStackTrace()
                null
            }
        }
    }
    println("PRODUCE STATE: $image")
    println("URL: $url")
    if (image != null) {
        Image(
            painter = painterFor(image!!),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

@Composable
fun AsyncImageChanged(
    url: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
//    val image: String? by produceState<String?>(null) {
//        value = url
//        value = withContext(Dispatchers.IO) {
//            try {
//                url
////                loadBufferedInputStream(url)
//            } catch (e: IOException) {
//                // instead of printing to console, you can also write this to log,
//                // or show some error placeholder
//                e.printStackTrace()
//                null
//            }
//        }
//    }


    var rememberUrl: String? by remember { mutableStateOf(null) }
    rememberUrl = url
//    var painter: BitmapPainter? by remember { mutableStateOf(null) }

    val produceImage by produceState<AsyncImageSource>(AsyncImageSource.No) {
        value = AsyncImageSource.Has(url)
    }



//    val flowImage: AsyncImageSource by getByUrl(url).collectAsState(AsyncImageSource.No)

//    coroutine.launch {
//        if (rememberUrl != url) {
//            rememberUrl = url
//            painter = BitmapPainter(loadBufferedInputStream(url).use {
//                println("TEST: loading painter")
//                loadImageBitmap(it)
//            })
//            delay(1500)
//            println("TEST: painter $painter")
//        }
//    }

    when (val i = produceImage) {
        AsyncImageSource.No -> {}
        is AsyncImageSource.Has -> {
            Text(i.url)
            Image(
                painter = BitmapPainter(loadBufferedInputStream(i.url).use {
                    println("TEST: loading painter")
                    loadImageBitmap(it)
                }),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier
            )
        }
    }

//    if (rememberUrl != null) {
//
//    }
}

@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    placeholderContent: @Composable (() -> Unit)? = null,
) {
    val image: T? by produceState<T?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                load()
            } catch (e: IOException) {
                // instead of printing to console, you can also write this to log,
                // or show some error placeholder
                e.printStackTrace()
                null
            }
        }
    }

    Box(modifier) {
        if (image != null) {
            Image(
                painter = painterFor(image!!),
                contentDescription = contentDescription,
                contentScale = contentScale
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


@Stable
sealed class AsyncImageSource {
    @Stable
    object No : AsyncImageSource()
    @Stable
    class Has(val url: String,val timestamp:Long = System.currentTimeMillis()) : AsyncImageSource()
}

fun getByUrl(url: String): Flow<AsyncImageSource> = flow {
    emit(AsyncImageSource.Has(url))
}