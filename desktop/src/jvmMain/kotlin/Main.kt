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
import com.denchic45.kts.ui.theme.KtsTheme
import com.denchic45.kts.ui.timetable.desktopConfig
import io.kamel.image.config.LocalKamelConfig
import java.awt.Toolkit

val appComponent = JvmAppComponent::class.create(
    PreferencesComponent::class.create(SettingsFactory()),
    DatabaseComponent::class.create(DriverFactory()),
    NetworkComponent::class.create()
)

val splashComponent = appComponent.splashComponent

fun main() = application {

    val isAuth by splashComponent.isAuth.collectAsState(null)

    isAuth?.let {
        if (it) {
            val size = Toolkit.getDefaultToolkit().screenSize.run {
                DpSize((width - 124).dp, (height - 124).dp)
            }

            Window(
                onCloseRequest = ::exitApplication,
                state = WindowState(size = size, position = WindowPosition(Alignment.Center))
            ) {
                KtsTheme {
                    CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
                        MainContent(appComponent.rootComponent())
                    }
                }
            }
        } else {
            Window(onCloseRequest = ::exitApplication,
                state = WindowState(size = DpSize(Dp.Unspecified, Dp.Unspecified))
            ) {
                LoginScreen(appComponent.loginComponent())
            }
        }
    }
}

//private fun testDatabase(users: List<String>) {
//    var users1 = users
//    GlobalScope.launch {
//        delay(500)
//        DriverFactory().driver.apply {
//            DbHelper(this).database.apply {
//                userEntityQueries.upsert(
//                    UserEntity(
//                        user_id = "1",
//                        first_name = "Ivan",
//                        surname = "Ivanoov",
//                        patronymic = null,
//                        user_group_id = "",
//                        role = "STUDENT",
//                        email = null,
//                        photo_url = "",
//                        gender = 1,
//                        admin = false,
//                        generated_avatar = true,
//                        timestamp = 0
//                    )
//                )
//                userEntityQueries
//                    .getAll()
//                    .asFlow()
//                    .mapToList()
//                    .collect { users1 = it.map { "User: ${it.first_name + it.surname}" } }
//            }
//        }
//    }
//}