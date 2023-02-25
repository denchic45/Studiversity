package com.denchic45.kts

import com.denchic45.kts.data.domain.model.DomainModel


class UIEditor<T>(
    val isNew: Boolean = true,
    val createItem: () -> T = { throw Throwable("Nothing create item lambda!") },
) {

    val item: T
        get() = createItem()

    var oldItem: T? = null

    val hasBeenChanged: () -> Boolean = { oldItem != createItem() }

}