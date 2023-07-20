package com.denchic45.studiversity.di

import com.denchic45.studiversity.data.db.local.DriverFactory
import com.denchic45.studiversity.data.preference.AppPreferences
import com.denchic45.studiversity.data.service.AppVersionService
import com.denchic45.studiversity.data.service.FakeAppVersionService
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

    @AppScope
    @Provides
    fun provideSystemDirs() = SystemDirs()

    @AppScope
    @Provides
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    abstract val appPreferences: AppPreferences

    @AppScope
    @Provides
    fun provideAppVersionService(coroutineScope: CoroutineScope): AppVersionService {
        return FakeAppVersionService(coroutineScope)
    }
}