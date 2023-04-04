package com.denchic45.kts

import android.app.Application
import com.denchic45.kts.data.db.local.DriverFactory
import com.denchic45.kts.di.*
import com.denchic45.kts.di.component.AndroidAppComponent
import com.denchic45.kts.di.component.create
import io.ktor.client.engine.android.*
import kotlin.properties.Delegates

var app: AndroidApp by Delegates.notNull()
    private set

class AndroidApp : Application() {
    val appComponent by lazy {
        AndroidAppComponent::class.create(
            this,
            PreferencesComponent::class.create(SettingsFactory(applicationContext)),
            DatabaseComponent::class.create(DriverFactory(applicationContext)),
            NetworkComponent::class.create(Android)
        )
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}