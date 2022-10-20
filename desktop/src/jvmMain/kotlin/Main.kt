import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.denchic45.kts.util.AsyncImageOriginal
import com.denchic45.kts.util.loadFileImageBitmap
import java.awt.Toolkit

val appComponent = JvmAppComponent::class.create(
    PreferencesComponent::class.create(SettingsFactory()),
    DatabaseComponent::class.create(DriverFactory()),
    NetworkComponent::class.create()
)

val splashComponent = appComponent.splashComponent

fun main() = asyncImages()

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

fun asyncImages() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(size = DpSize(500.dp, 500.dp))
    ) {
        val dir = "/home/denis/Documents"
        val images = listOf("$dir/image1.png", "$dir/image2.png", "$dir/image3.png")
        var currentImageUrl by remember { mutableStateOf<String?>(null) }

        Column {
            Row {
                Button(onClick = { currentImageUrl = null }) { Text("null") }
                Button(onClick = { currentImageUrl = images[0] }) { Text("0") }
                Button(onClick = { currentImageUrl = images[1] }) { Text("1") }
                Button(onClick = { currentImageUrl = images[2] }) { Text("2") }
                Text("Current image: $currentImageUrl")
            }
            currentImageUrl?.let { url ->
                AsyncImageOriginal(
                    url = url,
                    load = { loadFileImageBitmap(url) },
                    painterFor =  { BitmapPainter(it) } ,
                    ""
                )
//                AsyncImageChanged(url = url, "")
            }
        }
    }
}