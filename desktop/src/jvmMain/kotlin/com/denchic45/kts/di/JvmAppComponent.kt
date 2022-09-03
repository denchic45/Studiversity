package com.denchic45.kts.di

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.FakeAppVersionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import javax.inject.Singleton

@Component
abstract class JvmAppComponent(
    @Component val preferencesComponent: PreferencesComponent,
    @Component val databaseComponent: DatabaseComponent,
    @Component val networkComponent: NetworkComponent,
) : LogicComponent {

    @Provides
    fun provideComponentContext(): ComponentContext {
        return DefaultComponentContext(LifecycleRegistry())
    }

    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    fun provideAppVersionService(coroutineScope: CoroutineScope): AppVersionService {
        return FakeAppVersionService(coroutineScope)
    }
}

