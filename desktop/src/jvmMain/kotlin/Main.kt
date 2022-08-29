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
import com.denchic45.kts.UserEntity
import com.denchic45.kts.data.db.local.DbHelper
import com.denchic45.kts.data.db.local.DriverFactory
import com.denchic45.kts.di.SettingsFactory
import com.denchic45.kts.di.component.JvmAppComponent
import com.denchic45.kts.di.component.PreferencesComponent
import com.denchic45.kts.di.component.create
import com.denchic45.kts.ui.MainContent
import com.denchic45.kts.ui.login.LoginScreen
import com.denchic45.kts.ui.theme.KtsTheme
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Toolkit

fun main() = application {
    val appComponent = JvmAppComponent::class.create(
        PreferencesComponent::class.create(SettingsFactory())
    )

    val rootComponent = appComponent.rootComponent

    val isAuth by rootComponent.isAuth.collectAsState(null)

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
                    MainContent(rootComponent)
                }
            }
        } else {
            Window(onCloseRequest = ::exitApplication,
                state = WindowState(size = DpSize(Dp.Unspecified, Dp.Unspecified))
            ) {
                LoginScreen(appComponent.loginComponent)
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