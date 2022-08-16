package com.denchic45.kts

import com.denchic45.kts.di.SettingsFactory
import com.denchic45.kts.di.component.DaggerAndroidAppComponent
import com.denchic45.kts.di.module.AndroidAppModule
import com.denchic45.kts.di.module.PreferencesModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class AndroidApp : DaggerApplication() {
    private val androidAppComponent: AndroidInjector<AndroidApp> = DaggerAndroidAppComponent.builder()
        .appModule(AndroidAppModule(this))
        .preferenceModule(PreferencesModule(SettingsFactory(this)))
        .create(this)

    override fun onCreate() {
        super.onCreate()
        androidAppComponent.inject(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return androidAppComponent
    }
}