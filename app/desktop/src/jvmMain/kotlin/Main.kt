import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.kts.di.*
import com.denchic45.kts.ui.MainContent
import com.denchic45.kts.ui.login.LoginScreen
import com.denchic45.kts.ui.theme.AppTheme
import java.awt.Toolkit

val splashComponent = appComponent.splashComponent

fun main() = mainApp()

@OptIn(ExperimentalDecomposeApi::class)
private fun mainApp() {
    val lifecycle = LifecycleRegistry()
    appComponent.componentContext = DefaultComponentContext(lifecycle)
    application {
        val isAuth by splashComponent.isAuth.collectAsState(null)

        isAuth?.let {
            if (it) {
                val size = Toolkit.getDefaultToolkit().screenSize.run {
                    DpSize((width - 124).dp, (height - 124).dp)
                }
                val state =
                    rememberWindowState(size = size, position = WindowPosition(Alignment.Center))
                LifecycleController(lifecycle, state)

                Window(
                    title = "Studiversity",
                    onCloseRequest = ::exitApplication,
                    state = state
                ) {
                    AppTheme {
                        CompositionLocalProvider {
                            MainContent(appComponent.mainComponent())
                        }
                    }
                }
            } else {
                Window(
                    title = "Studiversity - Авторизация",
                    onCloseRequest = ::exitApplication,
                    state = rememberWindowState(size = DpSize(Dp.Unspecified, Dp.Unspecified))
                ) {
                    AppTheme { LoginScreen(appComponent.loginComponent()) }
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