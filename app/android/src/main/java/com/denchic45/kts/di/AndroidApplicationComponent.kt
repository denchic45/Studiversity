package com.denchic45.kts.di

import android.app.Application
import android.content.Context
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.storage.FileProvider
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.fab.FabInteractor
import com.denchic45.kts.util.SystemDirs
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

    @AppScope
    @Provides
    fun provideSystemDirs(context: Context) = SystemDirs(context)

//    @Provides
//    fun componentContext(): ComponentContext = componentContext

    @AppScope
    @Provides
    fun applicationScope() = CoroutineScope(SupervisorJob())

    @AppScope
    @Provides
    fun fileProvider(context: Context) = FileProvider(context.contentResolver)

    protected abstract val appPreferences: AppPreferences

    abstract val appBarInteractor: AppBarInteractor

    abstract val fabInteractor: FabInteractor

    abstract val confirmInteractor: ConfirmDialogInteractor

    abstract val injectFragmentFactory: InjectFragmentFactory

//    abstract val timetableLoaderComponent: TimetableLoaderComponent

//    abstract val yourTimetablesComponent: YourTimetablesComponent

//    abstract val yourStudyGroupsComponent: YourStudyGroupsComponent
}

