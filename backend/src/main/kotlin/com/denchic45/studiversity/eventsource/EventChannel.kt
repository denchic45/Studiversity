package com.denchic45.studiversity.eventsource

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import java.util.*
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
abstract class EventChannel<T : Event> : EventEmitter<T>, EventSource<T> {
    private val sources: MutableMap<KClass<out T>, MutableSharedFlow<out T>> = mutableMapOf()

    override suspend fun <C : T> emit(event: C) {
        println("emit by type: ${event::class}")
        getOrCreateSourceByEventType(event::class).emit(event)
    }

    override suspend fun <C : T> on(type: KClass<C>, block: (event: C) -> Unit) {
        getOrCreateSourceByEventType(type).collect(block)
    }

    private fun <C : T> getOrCreateSourceByEventType(type: KClass<out C>): MutableSharedFlow<C> {
        return sources.getOrPut(type, ::MutableSharedFlow) as MutableSharedFlow<C>
    }

    private fun <C : T> getSourceByEventType(type: KClass<out C>): MutableSharedFlow<C>? {
        return sources[type] as MutableSharedFlow<C>?
    }
}

interface EventEmitter<T : Event> {
    suspend fun <C : T> emit(event: C)
}

interface EventSource<T : Event> {

    suspend fun <C : T> on(type: KClass<C>, block: (event: C) -> Unit)
}

@OptIn(DelicateCoroutinesApi::class)
fun main(): Unit = runBlocking {
    val channel = UserChannel()
    GlobalScope.launch {
        channel.on(UserEvent.CreateUser::class) {
            println("on: $it")
        }
    }
    channel.emit(UserEvent.CreateUser(UUID.randomUUID()))

    delay(1000)
}

class UserChannel : EventChannel<UserEvent>()

sealed class UserEvent : Event {
    data class CreateUser(private val userId: UUID) : UserEvent()
    data class DeleteUser(private val userId: UUID) : UserEvent()
}