package com.denchic45.studiversity.uivalidator.experimental

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Condition<T>.triggerOn(flow: Flow<*>, coroutineScope: CoroutineScope) = apply {
    coroutineScope.launch { flow.collect { validate() } }
}