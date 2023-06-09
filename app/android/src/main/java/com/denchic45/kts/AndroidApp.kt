package com.denchic45.kts

import androidx.work.Configuration
import com.denchic45.kts.data.db.local.DriverFactory
import com.denchic45.kts.di.AndroidApplicationComponent
import com.denchic45.kts.di.DatabaseComponent
import com.denchic45.kts.di.NetworkComponent
import com.denchic45.kts.di.PreferencesComponent
import com.denchic45.kts.di.SettingsFactory
import com.denchic45.kts.di.component.DaggerAppComponent
import com.denchic45.kts.di.create
import com.denchic45.kts.di.module.AndroidAppModule
import com.denchic45.kts.di.module.PreferencesModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.ktor.client.engine.android.Android
import kotlin.properties.Delegates

var app: AndroidApp by Delegates.notNull()
    private set

class AndroidApp : DaggerApplication(), Configuration.Provider {
    private val androidAppComponent: AndroidInjector<AndroidApp> =
        DaggerAppComponent.builder()
            .appModule(AndroidAppModule(this))
            .preferencesModule(PreferencesModule(SettingsFactory(this)))
            .create(this)

    val appComponent: AndroidApplicationComponent by lazy {
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

        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return androidAppComponent
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(appComponent.workFactory).build()
    }
}