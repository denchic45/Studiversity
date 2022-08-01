package com.denchic45.kts.domain.model

import com.denchic45.kts.data.domain.model.EventType
import com.denchic45.kts.domain.DomainModel

abstract class EventDetails : DomainModel {
    abstract val eventType: EventType
    override fun copy(): EventDetails {
        return EmptyEventDetails()
    }

    override val id: String = ""
}