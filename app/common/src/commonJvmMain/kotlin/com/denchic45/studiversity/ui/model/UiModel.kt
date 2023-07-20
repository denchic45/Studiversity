package com.denchic45.studiversity.ui.model

import com.denchic45.studiversity.domain.model.DomainModel
import java.util.UUID

interface UiModel : DomainModel {
    override val id: UUID
    get() = UUID.randomUUID() // TODO: убрать потом
}