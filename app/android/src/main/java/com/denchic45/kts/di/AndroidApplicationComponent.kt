package com.denchic45.kts.di

import android.app.Application
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.storage.AttachmentStorage
import com.denchic45.kts.data.storage.FileProvider
import com.denchic45.kts.data.workmanager.AppWorkerFactory
import com.denchic45.kts.data.workmanager.DownloadWorker
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.fab.FabInteractor
import com.denchic45.kts.util.SystemDirs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.IntoMap
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

    @AppScope
    @Provides
    fun applicationScope() = CoroutineScope(SupervisorJob())

    protected abstract val appPreferences: AppPreferences

    @AppScope
    @Provides
    fun fileProvider(context: Context) = FileProvider(context.contentResolver)

    abstract val allWorkers: Map<Class<out ListenableWorker>, (Context, WorkerParameters) -> ListenableWorker>

    @IntoMap
    @Provides
    protected fun downloadWorker(
        storage: AttachmentStorage
    ): Pair<Class<out ListenableWorker>, (Context, WorkerParameters) -> ListenableWorker> {
        return DownloadWorker::class.java to { context, params ->
            DownloadWorker(context, params, storage)
        }
    }

    abstract val workFactory: AppWorkerFactory

    abstract val appBarInteractor: AppBarInteractor

    abstract val fabInteractor: FabInteractor

    abstract val confirmInteractor: ConfirmDialogInteractor

    abstract val injectFragmentFactory: InjectFragmentFactory
}
