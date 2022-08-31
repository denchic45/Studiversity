package com.denchic45.kts.di.module

import android.app.Application
import android.content.Context
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.appVersion.GoogleAppVersionService
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import javax.inject.Singleton


@Module(
    includes = [AndroidAppBindModule::class]
)
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

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://localhost/")
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpClient() = HttpClient(Android) {
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
interface AndroidAppBindModule {
    @Singleton
    @Binds
    fun bindAppVersionService(googleAppVersionService: GoogleAppVersionService): AppVersionService

}