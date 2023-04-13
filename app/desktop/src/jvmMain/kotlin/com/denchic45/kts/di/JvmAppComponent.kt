package com.denchic45.kts.di

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.denchic45.kts.data.db.local.DriverFactory
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.FakeAppVersionService
import com.denchic45.kts.ui.MainComponent
import com.denchic45.kts.ui.login.LoginComponent
import com.denchic45.kts.ui.navigation.OverlayConfig
import com.denchic45.kts.ui.splash.SplashComponent
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

val appComponent = JvmAppComponent::class.create(
    PreferencesComponent::class.create(SettingsFactory()),
    DatabaseComponent::class.create(DriverFactory()),
    NetworkComponent::class.create(CIO)
)

@AppScope
@Component
abstract class JvmAppComponent(
    @Component val preferencesComponent: PreferencesComponent,
    @Component val databaseComponent: DatabaseComponent,
    @Component val networkComponent: NetworkComponent
) {

//    lateinit var componentContext: ComponentContext

//    @Provides
//    fun provideComponentContext(): ComponentContext {
//        return componentContext
//    }

    @AppScope
    @Provides
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

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

    abstract val appPreferences: AppPreferences

    abstract val splashComponent: SplashComponent

    abstract val mainComponent: (ComponentContext) -> MainComponent

    abstract val loginComponent: (ComponentContext) -> LoginComponent
}

