package com.denchic45.studiversity.util

import com.arkivanov.decompose.router.children.NavigationSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

fun <T : Any> NavigationSource<T>.asFlow(): Flow<T> = callbackFlow {
    val observer: (T) -> Unit = {
      trySendBlocking(it)
    }
    subscribe(observer)
    awaitClose {
        unsubscribe(observer)
    }
}.conflate()