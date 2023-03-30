package com.denchic45.kts.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> Flow<T>.collectWhen(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State,
    action: FlowCollector<T> = FlowCollector {}
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state) {
            this@collectWhen.collect(action)
        }
    }
}

fun <T> Flow<T>.collectWhenCreated(
    lifecycleOwner: LifecycleOwner,
    action: FlowCollector<T> = FlowCollector {}
) = collectWhen(lifecycleOwner, Lifecycle.State.CREATED, action)

fun <T> Flow<T>.collectWhenStarted(
    lifecycleOwner: LifecycleOwner,
    action: FlowCollector<T> = FlowCollector {}
) = collectWhen(lifecycleOwner, Lifecycle.State.STARTED, action)

fun <T> Flow<T>.collectWhenResumed(
    lifecycleOwner: LifecycleOwner,
    action: FlowCollector<T> = FlowCollector {}
) = collectWhen(lifecycleOwner, Lifecycle.State.RESUMED, action)