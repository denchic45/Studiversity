package com.denchic45.kts.di

import android.app.Application
import android.content.Context
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.ui.appbar.AppBarInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides


@AppScope
@Component
abstract class AndroidApplicationComponent(
    @get:Provides protected val application: Application,
    @Component protected val preferencesComponent: PreferencesComponent,
    @Component protected val databaseComponent: DatabaseComponent,
    @Component protected val networkComponent: NetworkComponent,
) {

    @Provides
    fun context(): Context = application

//    @Provides
//    fun componentContext(): ComponentContext = componentContext

    @AppScope
    @Provides
    fun applicationScope() = CoroutineScope(SupervisorJob())

    protected abstract val appPreferences: AppPreferences

    abstract val appBarInteractor: AppBarInteractor

    abstract val injectFragmentFactory: InjectFragmentFactory

//    abstract val timetableLoaderComponent: TimetableLoaderComponent

//    abstract val yourTimetablesComponent: YourTimetablesComponent

//    abstract val yourStudyGroupsComponent: YourStudyGroupsComponent
}

