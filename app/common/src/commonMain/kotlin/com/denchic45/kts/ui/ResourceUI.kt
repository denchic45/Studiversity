package com.denchic45.kts.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.denchic45.kts.data.domain.Failure
import com.denchic45.kts.domain.Resource

@Composable
fun <T> ResourceContent(
    resource: Resource<T>,
    onLoading: @Composable () -> Unit = { CircularLoadingBox(Modifier.fillMaxSize()) },
    onError: @Composable (Failure) -> Unit = {},
    onSuccess: @Composable (T) -> Unit,
) {
    when (resource) {
        Resource.Loading -> onLoading()
        is Resource.Success -> onSuccess(resource.value)
        is Resource.Error -> onError(resource.failure)
    }
}

@Composable
fun CircularLoadingBox(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun LinearLoadingBox(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        LinearProgressIndicator()
    }
}