package com.denchic45.kts.ui.model

import com.denchic45.kts.data.domain.model.DomainModel
import java.util.UUID

interface UiModel : DomainModel {
    override val id: UUID
    get() = UUID.randomUUID() // TODO: убрать потом
}