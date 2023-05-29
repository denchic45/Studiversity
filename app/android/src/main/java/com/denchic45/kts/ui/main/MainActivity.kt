package com.denchic45.kts.ui.main

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.app
import com.denchic45.kts.ui.theme.AppTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContent {
            AppTheme {
                MainScreen(
                    component = app.appComponent.mainComponent(defaultComponentContext()),
                    activity = this,
                    confirmDialogInteractor = app.appComponent.confirmDialogInteractor
                )
            }
        }
    }
}