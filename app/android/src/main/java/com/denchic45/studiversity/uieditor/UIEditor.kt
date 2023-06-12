package com.denchic45.studiversity.uieditor

import android.util.Log
import com.denchic45.studiversity.data.domain.model.DomainModel

class UIEditor<T> @JvmOverloads constructor(
    val isNew: Boolean = true,
    val createItem: () -> T = { throw Throwable("Nothing create item lambda!") },
) {
    val item: T
        get() = createItem()
    var oldItem: T? = null
    fun hasBeenChanged(): Boolean {
        Log.d("lol", "hasBeenChanged course: $oldItem \nand ${createItem()}")
        return oldItem != createItem()
    }
}