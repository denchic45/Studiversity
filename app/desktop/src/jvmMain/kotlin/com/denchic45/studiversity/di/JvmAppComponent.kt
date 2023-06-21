package com.denchic45.studiversity.di

import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.denchic45.studiversity.data.db.local.DriverFactory
import com.denchic45.studiversity.data.pref.AppPreferences
import com.denchic45.studiversity.data.service.AppVersionService
import com.denchic45.studiversity.data.service.FakeAppVersionService
import com.denchic45.studiversity.ui.navigation.OverlayConfig
import com.denchic45.studiversity.util.SystemDirs
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

val appComponent = JvmAppComponent::class.create(
    PreferencesComponent::class.create(SettingsFactory()),
    DatabaseComponent::class.create(DriverFactory()),
    NetworkComponent::class.create(CIO)
)


@Component
abstract class JvmAppComponent(
    @Component val preferencesComponent: PreferencesComponent,
    @Component val databaseComponent: DatabaseComponent,
    @Component val networkComponent: NetworkComponent
) : CommonApplicationComponent() {

//    lateinit var componentContext: ComponentContext

//    @Provides
//    fun provideComponentContext(): ComponentContext {
//        return componentContext
//    }

    @AppScope
    @Provides
    fun provideSystemDirs() = SystemDirs()

    @AppScope
    @Provides
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    abstract val appPreferences: AppPreferences

    @AppScope
    @Provides
    fun provideOverlayNavigator(): OverlayNavigation<OverlayConfig> {
        return OverlayNavigation()
    }

    @AppScope
    @Provides
    fun provideAppVersionService(coroutineScope: CoroutineScope): AppVersionService {
        return FakeAppVersionService(coroutineScope)
    }
}