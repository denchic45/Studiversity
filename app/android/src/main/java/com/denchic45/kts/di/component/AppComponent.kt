package com.denchic45.kts.di.component

import com.denchic45.kts.AndroidApp
import com.denchic45.kts.di.NetworkModule
import com.denchic45.kts.di.module.*
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AndroidAppModule::class,
        DatabaseModule::class,
        StorageModule::class,
        PreferencesModule::class,
        RawModule::class,
        NetworkModule::class,
        DispatcherModule::class,
        FragmentModule::class,
        ActivityModule::class,
        AndroidInjectionModule::class
    ]
)
interface AppComponent : AndroidInjector<AndroidApp> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<AndroidApp>() {
        abstract fun appModule(androidAppModule: AndroidAppModule): Builder
        abstract fun preferencesModule(preferenceModule: PreferencesModule):Builder
        abstract override fun build(): AppComponent
    }
}