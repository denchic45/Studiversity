package com.denchic45.studiversity.eventsource

data class EventResource<T : Event>(
    val header: String,
    val event: T
)