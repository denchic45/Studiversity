package com.denchic45.studiversity

import android.app.Application
import androidx.work.Configuration
import com.denchic45.studiversity.data.db.local.DriverFactory
import com.denchic45.studiversity.di.AndroidAppComponent
import com.denchic45.studiversity.di.DatabaseComponent
import com.denchic45.studiversity.di.NetworkComponent
import com.denchic45.studiversity.di.PreferencesComponent
import com.denchic45.studiversity.di.SettingsFactory
import com.denchic45.studiversity.di.create
import io.ktor.client.engine.android.Android
import kotlin.properties.Delegates

var app: AndroidApp by Delegates.notNull()
    private set

class AndroidApp : Application(), Configuration.Provider {
    val appComponent: AndroidAppComponent by lazy {
        AndroidAppComponent::class.create(
            this,
            PreferencesComponent::class.create(SettingsFactory(applicationContext)),
            DatabaseComponent::class.create(DriverFactory(applicationContext)),
            NetworkComponent::class.create(Android)
        )
    }

    override fun onCreate() {
        super.onCreate()
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
        app = this
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(appComponent.workFactory).build()
    }
}