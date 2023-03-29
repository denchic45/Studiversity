package com.denchic45.kts.di.component

import com.denchic45.kts.di.AppScope
import com.denchic45.kts.di.DatabaseComponent
import com.denchic45.kts.di.NetworkComponent
import com.denchic45.kts.di.PreferencesComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@AppScope
@Component
abstract class AndroidAppComponent(
    @Component val preferencesComponent: PreferencesComponent,
    @Component val databaseComponent: DatabaseComponent,
    @Component val networkComponent: NetworkComponent,
    @Component val viewModelComponent:ViewModelComponent
) {

    @AppScope
    @Provides
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

