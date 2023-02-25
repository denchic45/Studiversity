package com.denchic45.kts.data.domain.model

import java.util.UUID

interface DomainModel : Equatable {

     val id: UUID

    fun copy(): DomainModel {
        throw IllegalStateException("You must override copy function")
    }
}
