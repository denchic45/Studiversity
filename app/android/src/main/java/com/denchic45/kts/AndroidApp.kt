package com.denchic45.kts

import com.denchic45.kts.data.db.local.DriverFactory
import com.denchic45.kts.di.*
import com.denchic45.kts.di.component.AndroidApplicationComponent
import com.denchic45.kts.di.component.DaggerAppComponent
import com.denchic45.kts.di.component.create
import com.denchic45.kts.di.module.AndroidAppModule
import com.denchic45.kts.di.module.PreferencesModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.ktor.client.engine.android.*
import kotlin.properties.Delegates

var app: AndroidApp by Delegates.notNull()
    private set

class AndroidApp : DaggerApplication() {
    private val androidAppComponent: AndroidInjector<AndroidApp> =
        DaggerAppComponent.builder()
            .appModule(AndroidAppModule(this))
            .preferencesModule(PreferencesModule(SettingsFactory(this)))
            .create(this)

    val appComponent by lazy {
        AndroidApplicationComponent::class.create(
            this,
            PreferencesComponent::class.create(SettingsFactory(applicationContext)),
            DatabaseComponent::class.create(DriverFactory(applicationContext)),
            NetworkComponent::class.create(Android)
        )
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        androidAppComponent.inject(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return androidAppComponent
    }
}