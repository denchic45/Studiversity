//package com.denchic45.kts.di.component
//
//import com.denchic45.kts.di.module.DesktopAppModule
//import com.denchic45.kts.di.module.PreferencesModule
//import com.denchic45.kts.ui.root.RootComponent
//import dagger.Component
//import javax.inject.Singleton
//
//@Singleton
//@Component(
//    modules = [
//        DesktopAppModule::class,
////        DatabaseModule::class,
////        StorageModule::class,
////        MapperModule::class,
//        PreferencesModule::class,
////        RawModule::class,
////        DispatcherModule::class,
////        FragmentModule::class,
////        ActivityModule::class,
////        AndroidInjectionModule::class
//    ]
//)
//interface DesktopAppComponent {
//
//    fun rootComponent(): RootComponent
//
//    @Component.Builder
//    interface Builder {
//        fun appModule(desktopAppModule: DesktopAppModule): Builder
//        fun preferencesModule(preferencesModule: PreferencesModule): Builder
//        fun build(): DesktopAppComponent
//    }
//}