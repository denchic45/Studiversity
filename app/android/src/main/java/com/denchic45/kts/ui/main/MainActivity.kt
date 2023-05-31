package com.denchic45.kts.ui.main

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.app
import com.denchic45.kts.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val defaultComponentContext = defaultComponentContext()
        val component = app.appComponent.mainComponent(defaultComponentContext)
        val confirmDialogInteractor = app.appComponent.confirmDialogInteractor
        setContent {
            AppTheme {
                MainScreen(
                    component = component,
                    activity = this,
                    confirmDialogInteractor = confirmDialogInteractor
                )
            }
        }
    }
}