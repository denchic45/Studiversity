package com.denchic45.kts.ui.model

import com.denchic45.kts.data.domain.model.DomainModel

interface UiModel : DomainModel {
    override val id: String
        get() = ""
}