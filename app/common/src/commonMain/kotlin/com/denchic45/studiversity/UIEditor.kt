package com.denchic45.studiversity


class UIEditor<T>(
    val isNew: Boolean = true,
    val createItem: () -> T = { throw Throwable("Nothing create item lambda!") },
) {

    val item: T
        get() = createItem()

    var oldItem: T? = null

    val hasBeenChanged: () -> Boolean = { oldItem != createItem() }

}