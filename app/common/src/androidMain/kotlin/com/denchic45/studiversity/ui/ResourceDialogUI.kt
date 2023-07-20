package com.denchic45.studiversity.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.denchic45.studiversity.domain.Failure
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun <T> ResourceDialogContent(
    resource: Resource<T>,
    onDismissRequest: () -> Unit,
    onLoading: @Composable () -> Unit = {
        Dialog(onDismissRequest = onDismissRequest) {
            CircularLoadingBox(Modifier.fillMaxWidth())
        }
    },
    onError: @Composable (Failure) -> Unit = {},
    onSuccess: @Composable (T) -> Unit,
) {
    ResourceContent(resource, onLoading, onError, onSuccess)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    text: @Composable () -> Unit = { Text(text = "Загрузка") },
    onDismissRequest: () -> Unit
) {

    AlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.normal))
                text()
            }
        }
    }
}