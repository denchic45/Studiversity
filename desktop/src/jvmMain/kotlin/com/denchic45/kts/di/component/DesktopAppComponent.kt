package com.denchic45.kts.di.component

import com.denchic45.kts.di.module.DesktopAppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DesktopAppModule::class,
//        DatabaseModule::class,
//        StorageModule::class,
//        MapperModule::class,
//        PreferenceModule::class,
//        RawModule::class,
//        DispatcherModule::class,
//        FragmentModule::class,
//        ActivityModule::class,
//        AndroidInjectionModule::class
    ]
)
interface DesktopAppComponent {
    @Component.Builder
    interface Builder {
        fun appModule(desktopAppModule: DesktopAppModule): Builder
        fun build(): DesktopAppComponent
    }
}