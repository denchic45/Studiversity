package com.denchic45.kts.di

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.FakeAppVersionService
import com.denchic45.kts.ui.navigation.OverlayConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@AppScope
@Component
abstract class JvmAppComponent(
    @Component val preferencesComponent: PreferencesComponent,
    @Component val databaseComponent: DatabaseComponent,
    @Component val networkComponent: NetworkComponent
) : LogicComponent {

    @Provides
    fun provideComponentContext(): ComponentContext {
        return DefaultComponentContext(LifecycleRegistry())
    }

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
}

