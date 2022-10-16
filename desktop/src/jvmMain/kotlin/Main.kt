import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.denchic45.kts.ui.validationtest.ValidationTestScreen
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
            AppTheme {
                ValidationTestScreen()
            }
        }
    }
}
