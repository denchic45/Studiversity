package com.denchic45.kts.di.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.di.AppScope
import com.denchic45.kts.di.DatabaseComponent
import com.denchic45.kts.di.NetworkComponent
import com.denchic45.kts.di.PreferencesComponent
import com.denchic45.kts.ui.timetableLoader.TimetableLoaderComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides


@AppScope
@Component
abstract class AndroidAppComponent(
    @Component protected val preferencesComponent: PreferencesComponent,
    @Component protected val databaseComponent: DatabaseComponent,
    @Component protected val networkComponent: NetworkComponent
) {

    @Provides
    fun componentContext(): ComponentContext {
        return DefaultComponentContext(LifecycleRegistry())
    }

    @AppScope
    @Provides
    fun applicationScope() = CoroutineScope(SupervisorJob())

    protected abstract val appPreferences: AppPreferences

    abstract val timetableLoaderComponent: TimetableLoaderComponent
}

