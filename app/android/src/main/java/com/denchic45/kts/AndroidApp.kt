package com.denchic45.kts

import android.app.Application
import com.denchic45.kts.data.db.local.DriverFactory
import com.denchic45.kts.di.*
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.ktor.client.engine.android.*

class AndroidApp : Application() {
    val appComponent by lazy {
        AndroidAppComponent::class.create(
            PreferencesComponent::class.create(SettingsFactory(applicationContext)),
            DatabaseComponent::class.create(DriverFactory(applicationContext)),
            NetworkComponent::class.create(Android)
        )
    }
}