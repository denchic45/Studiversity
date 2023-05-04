package com.denchic45.kts.util

import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch

fun <T : Any> Value<T>.asFlow(): Flow<T> = callbackFlow {
    val observer: (T) -> Unit = {
      trySendBlocking(it)
    }
    subscribe(observer)
    awaitClose {
        unsubscribe(observer)
    }
}.conflate()