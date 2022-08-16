package com.denchic45.kts.di.module

import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.FakeAppVersionService
import com.denchic45.kts.di.component.DaggerDesktopAppComponent
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module(
    includes = [DesktopAppBindModule::class]
)
class DesktopAppModule {

    init {
        //TODO Переместить создание AppComponent в правильное место
        DaggerDesktopAppComponent.builder().appModule(DesktopAppModule()).build()
    }

    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideHttpClient() = HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }
}

@Module
interface DesktopAppBindModule {
    @Singleton
    @Binds
    fun bindAppVersionService(googleAppVersionService: FakeAppVersionService): AppVersionService

}