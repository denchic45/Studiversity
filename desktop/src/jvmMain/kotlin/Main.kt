import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.denchic45.kts.data.db.local.DriverFactory
import com.denchic45.kts.di.*
import com.denchic45.kts.ui.MainContent
import com.denchic45.kts.ui.login.LoginScreen
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.util.*
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.KamelImage
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.resourcesFetcher
import io.kamel.image.config.svgDecoder
import java.awt.Toolkit

val appComponent = JvmAppComponent::class.create(
    PreferencesComponent::class.create(SettingsFactory()),
    DatabaseComponent::class.create(DriverFactory()),
    NetworkComponent::class.create()
)

val splashComponent = appComponent.splashComponent

fun main() = mainApp()

private fun mainApp() {
    application {
        val isAuth by splashComponent.isAuth.collectAsState(null)

        isAuth?.let {
            if (it) {
                val size = Toolkit.getDefaultToolkit().screenSize.run {
                    DpSize((width - 124).dp, (height - 124).dp)
                }

                Window(
                    title = "KtsApp",
                    onCloseRequest = ::exitApplication,
                    state = WindowState(size = size, position = WindowPosition(Alignment.Center))
                ) {
                    AppTheme {
                        CompositionLocalProvider {
                            MainContent(appComponent.mainComponent())
                        }
                    }
                }
            } else {
                Window(
                    title = "KtsApp - Авторизация",
                    onCloseRequest = ::exitApplication,
                    state = WindowState(size = DpSize(Dp.Unspecified, Dp.Unspecified))
                ) {
                    AppTheme {
                        LoginScreen(appComponent.loginComponent())
                    }
                }
            }
        }
    }
}

fun previewUi() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = WindowState(size = DpSize(Dp.Unspecified, Dp.Unspecified))
        ) {

        }
    }
}

//fun asyncImages() = application {
//    Window(
//        onCloseRequest = ::exitApplication,
//        state = WindowState(size = DpSize(500.dp, 500.dp))
//    ) {
//
//        val images = listOf(
//            "https://img.freepik.com/free-vector/cute-cat-gaming-cartoon_138676-2969.jpg?w=740&t=st=1666244997~exp=1666245597~hmac=945f80054981a1990a36c3e6144f699332ac437a48bf5d82fb4e0104a9e934fb",
//            "https://img.freepik.com/free-vector/mysterious-mafia-man-smoking-cigarette_52683-34828.jpg?w=740&t=st=1666244997~exp=1666245597~hmac=8dd29f6fa84dad33ae855ef0fda60bedb2c7e4d88cac4a31a38ce2b20ebc699f",
//            "https://img.freepik.com/free-vector/cute-girl-gaming-holding-joystick-cartoon-icon-illustration-people-technology-icon-concept-isolated-flat-cartoon-style_138676-2169.jpg?w=740&t=st=1666244997~exp=1666245597~hmac=85d2beae496ae5eb95f9901a4144ebfc444a2a42f512d907fdd6584be4a635e5"
//        )
//
//        var currentImageUrl by remember { mutableStateOf<String?>(null) }
//
//        Column {
//            Row {
//                Button(onClick = { currentImageUrl = null }) { Text("null") }
//                Button(onClick = { currentImageUrl = images[0] }) { Text("0") }
//                Button(onClick = { currentImageUrl = images[1] }) { Text("1") }
//                Button(onClick = { currentImageUrl = images[2] }) { Text("2") }
//                Text("Current image: $currentImageUrl")
//            }
//            currentImageUrl?.let { url ->
//                AsyncImage(
//                    load = { loadFileImageBitmap(url) },
//                    painterFor = { BitmapPainter(it) },
//                    "",
//                    ""
//                )
//            }
//        }
//    }
//}

//fun kamelImages() = application {
//    Window(
//        onCloseRequest = ::exitApplication,
//        state = WindowState(size = DpSize(1500.dp, 500.dp))
//    ) {
//        println("Window")
//        val desktopConfig = KamelConfig {
//            takeFrom(KamelConfig.Default)
//            svgDecoder()
//            resourcesFetcher()
//        }
//
//        val images = listOf(
//            "https://sun9-46.userapi.com/impg/uWGoczB9st04meAMg6wWn4iSlmUXIjckTZNyqg/pb7GZSpZUAA.jpg?size=1080x2033&quality=96&sign=1320149c468c5ee8341e40a5f58f8923&type=album",
//            "https://img.freepik.com/free-vector/cute-girl-gaming-holding-joystick-cartoon-icon-illustration-people-technology-icon-concept-isolated-flat-cartoon-style_138676-2169.jpg",
//            "https://www.svgrepo.com/show/429115/wireless-hotspot-internet.svg",
//        )
//
//        var currentImageUrl by remember { mutableStateOf<String?>(null) }
//
//        Column {
//            println("Column")
//            Row {
//                Button(onClick = { currentImageUrl = null }) { Text("null") }
//                Button(onClick = { currentImageUrl = images[0] }) { Text("0") }
//                Button(onClick = { currentImageUrl = images[1] }) { Text("1") }
//                Button(onClick = { currentImageUrl = images[2] }) { Text("2") }
//                Text("Current image: $currentImageUrl")
//            }
//            CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
//                println("CompositionLocalProvider")
//                currentImageUrl?.let { url ->
//                    println("currentImageUrl")
//
//                    Row {
//
//                        Image(
//                            painter = lazyPainterUrl2(url),
//                            contentDescription = null
//                        )
//
//                        Spacer(Modifier.padding(horizontal = 48.dp))
//
//                        KamelImage(
//                            lazyPainterUrl(url),
//                            contentDescription = null,
//                            onFailure = { it.printStackTrace() },
//                            onLoading = {
//                                println("On loading: $it")
//                                Box(Modifier.size(100.dp).background(Color.Green))}
//                        )
//
//                        Spacer(Modifier.padding(horizontal = 48.dp))
//
//                        AsyncImage(
//                            key = url,
//                            load = { loadImageBitmap(url) },
//                            painterFor = { BitmapPainter(it) },
//                            contentDescription = ""
//                        ){ Box(Modifier.size(100.dp).background(Color.Green))}
//
//                        Spacer(Modifier.padding(horizontal = 48.dp))
//                    }
//                }
//            }
//        }
//    }
//}