package com.denchic45.kts.di.component

import com.denchic45.kts.AndroidApp
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
        AppModule::class,
        DatabaseModule::class,
        StorageModule::class,
        MapperModule::class,
        PreferencesModule::class,
        RawModule::class,
        DispatcherModule::class,
        FragmentModule::class,
        ActivityModule::class,
        AndroidInjectionModule::class
    ]
)
interface AndroidAppComponent : AndroidInjector<AndroidApp> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<AndroidApp>() {
        abstract fun appModule(appModule: AppModule): Builder
        abstract fun preferenceModule(preferenceModule: PreferencesModule):Builder
        abstract override fun build(): AndroidAppComponent
    }
}