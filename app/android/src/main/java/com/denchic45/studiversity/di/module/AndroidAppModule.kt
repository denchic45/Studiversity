package com.denchic45.studiversity.di.module

import android.app.Application
import android.content.Context
import com.denchic45.studiversity.appVersion.GoogleAppVersionService
import com.denchic45.studiversity.data.service.AppVersionService
import com.denchic45.studiversity.data.storage.FileProvider
import com.denchic45.studiversity.ui.appbar.AppBarInteractor
import com.denchic45.studiversity.util.SystemDirs
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import javax.inject.Singleton


@Module(includes = [AndroidAppBindModule::class])
class AndroidAppModule(private val application: Application) {


    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun providesApplication(): Application = application

    @Provides
    @Singleton
    fun providesApplicationContext(): Context = application

    @Singleton
    @Provides
    fun fileProvider(context: Context) = FileProvider(context.contentResolver)

    @Provides
    fun toolbarInteractor(): AppBarInteractor = AppBarInteractor()

//    @Provides
//    @Singleton
//    fun componentContext(): ComponentContext {
//        return DefaultComponentContext(LifecycleRegistry())
//    }

    @Singleton
    @Provides
    fun provideSystemDirs(context: Context) = SystemDirs(context)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://localhost/")
            .build()
    }

//    @Provides
//    @Singleton
//    fun provideHttpClient() = HttpClient(Android) {
//        install(Logging) {
//            level = LogLevel.ALL
//        }
//        install(ContentNegotiation) {
//            json(Json {
//                prettyPrint = true
//                isLenient = true
//            })
//        }
//    }
}

@Module
interface AndroidAppBindModule {
    @Singleton
    @Binds
    fun bindAppVersionService(googleAppVersionService: GoogleAppVersionService): AppVersionService

}