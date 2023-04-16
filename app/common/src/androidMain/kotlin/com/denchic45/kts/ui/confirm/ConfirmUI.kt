package com.denchic45.kts.ui.confirm

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.denchic45.kts.ui.get
import kotlinx.coroutines.launch

@Composable
fun ConfirmDialog(interactor: ConfirmDialogInteractor) {
    val state by interactor.stateFlow.collectAsState()
    state?.let {
        val coroutineScope = rememberCoroutineScope()
        AlertDialog(
            title = { Text(text = it.title.get(LocalContext.current)) },
            text = it.text?.let { { Text(text = it.get(LocalContext.current)) } },
            onDismissRequest = {
                coroutineScope.launch {
                    interactor.onConfirm(false)
                }
            },
            dismissButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        interactor.onConfirm(false)
                    }
                }) {
                    Text(text = "Отмена")
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        interactor.onConfirm(true)
                    }
                }) {
                    Text(text = "Ок")
                }
            }
        )
    }
}