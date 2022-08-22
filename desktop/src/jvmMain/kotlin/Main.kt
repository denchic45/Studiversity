import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.denchic45.kts.UserEntity
import com.denchic45.kts.data.db.local.DbHelper
import com.denchic45.kts.data.db.local.DriverFactory
import com.denchic45.kts.di.component.DeskAppComponent
import com.denchic45.kts.ui.MainContent
import com.denchic45.kts.ui.theme.KtsTheme
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

fun main() = application {
//    val appComponent = DaggerDesktopAppComponent
//        .builder()
//        .appModule(DesktopAppModule(DefaultComponentContext(LifecycleRegistry())))
//        .preferencesModule(PreferencesModule(SettingsFactory()))
//        .build()
//
//    val rootComponent = appComponent.rootComponent()

    val kClass: KClass<DeskAppComponent> = DeskAppComponent::class
    kClass.create()

    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(size = DpSize(1296.dp, 878.dp))
    ) {

        KtsTheme {
            MainContent()
//            users.forEach {
//                Text(it)
//            }
        }

        var users by remember { mutableStateOf<List<String>>(emptyList()) }
        testDatabase(users)
    }
}

private fun testDatabase(users: List<String>) {
    var users1 = users
    GlobalScope.launch {
        delay(500)
        DriverFactory().driver.apply {
            DbHelper(this).database.apply {
                userEntityQueries.upsert(
                    UserEntity(
                        user_id = "1",
                        first_name = "Ivan",
                        surname = "Ivanoov",
                        patronymic = null,
                        user_group_id = "",
                        role = "STUDENT",
                        email = null,
                        photo_url = "",
                        gender = 1,
                        admin = false,
                        generated_avatar = true,
                        timestamp = 0
                    )
                )
                userEntityQueries
                    .getAll()
                    .asFlow()
                    .mapToList()
                    .collect { users1 = it.map { "User: ${it.first_name + it.surname}" } }
            }
        }
    }
}