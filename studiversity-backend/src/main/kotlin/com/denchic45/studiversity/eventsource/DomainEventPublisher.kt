package com.denchic45.studiversity.eventsource

import kotlinx.coroutines.flow.MutableSharedFlow

class DomainEventPublisher<T : DomainEvent> : DomainEventSource<T> {

    private val source: MutableSharedFlow<T> = MutableSharedFlow()

    override suspend fun subscribe(block: (T) -> Unit) {
        source.collect(block)
    }
}

interface DomainEventSource<T : DomainEvent> {
    suspend fun subscribe(block: (event: T) -> Unit)
}