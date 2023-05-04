package com.denchic45.kts.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T, M> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> M,
): StateFlow<M> = map { mapper(it) }.stateIn(coroutineScope, SharingStarted.Eagerly, mapper(value))

//@OptIn(ExperimentalCoroutinesApi::class)
//fun <T, M> StateFlow<T>.flatMapLatest(
//    coroutineScope: CoroutineScope,
//    mapper: (value: T) -> StateFlow<M>,
//): StateFlow<M> = flatMapLatest { mapper(value) }
//    .stateIn(coroutineScope, SharingStarted.Eagerly, mapper(value).value)