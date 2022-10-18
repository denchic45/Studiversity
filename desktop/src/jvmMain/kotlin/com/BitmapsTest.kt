package com

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skiko.toBufferedImage
import org.jetbrains.skiko.toImage
import java.io.File

@Composable
fun createBitmap(imageBitmap: ImageBitmap): File {
    val width = 1000
    val bitmap = ImageBitmap(width, width).asSkiaBitmap()
//    bitmap.setImageInfo(ImageInfo(
//        2,
//        2,
//        colorType = ColorType.ARGB_4444,
//        alphaType = ColorAlphaType.UNKNOWN))
    bitmap.installPixels(
        ImageInfo.makeS32(width, width, ColorAlphaType.UNPREMUL),
        ByteArray(10000), (4L * width).toInt()
    )


//    val bitmap = imageBitmap.asSkiaBitmap()
//    if (!bitmap.allocPixels()) {
//        println("Couldn't allocate pixels for the image")
//    }

//    if (this.drawContext.canvas.nativeCanvas.readPixels(bitmap, 0, 0)) {
//        println("Couldn't read pixels from the canvas")
//    }

    val data = bitmap.toBufferedImage().toImage().encodeToData()
    val randFile = File("image.png")
    if (data != null) {
        randFile.writeBytes(data.bytes)
    } else {
        println("Data returned null")
    }
    return randFile
}

fun testBitmapUi() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = WindowState(size = DpSize(500.dp, 500.dp))
        ) {
            AsyncImage(
                load = { loadImageBitmap("https://sun1-99.userapi.com/impg/uWGoczB9st04meAMg6wWn4iSlmUXIjckTZNyqg/pb7GZSpZUAA.jpg?size=1080x2033&quality=96&sign=1320149c468c5ee8341e40a5f58f8923&type=album") },
                painterFor = {
                    createBitmap(it)
                    BitmapPainter(it)
                },
                null
            )
        }
    }
}