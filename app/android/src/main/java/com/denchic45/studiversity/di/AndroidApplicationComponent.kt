package com.denchic45.studiversity.di

import android.app.Application
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.studiversity.data.pref.AppPreferences
import com.denchic45.studiversity.data.storage.AttachmentStorage
import com.denchic45.studiversity.data.storage.FileProvider
import com.denchic45.studiversity.data.workmanager.AppWorkerFactory
import com.denchic45.studiversity.data.workmanager.DownloadWorker
import com.denchic45.studiversity.ui.MainComponent
import com.denchic45.studiversity.ui.appbar.AppBarInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.fab.FabInteractor
import com.denchic45.studiversity.ui.root.RootComponent
import com.denchic45.studiversity.util.SystemDirs
import io.ktor.client.HttpClient
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

    private val rootComponentContext = DefaultComponentContext(LifecycleRegistry())

    abstract val rootComponent: (ComponentContext) -> RootComponent

    abstract val mainComponent: (ComponentContext) -> MainComponent

    abstract val authedClient: HttpClient

    abstract val workFactory: AppWorkerFactory

    abstract val appBarInteractor: AppBarInteractor

    abstract val fabInteractor: FabInteractor

    abstract val confirmDialogInteractor: ConfirmDialogInteractor

    abstract val injectFragmentFactory: InjectFragmentFactory
}
