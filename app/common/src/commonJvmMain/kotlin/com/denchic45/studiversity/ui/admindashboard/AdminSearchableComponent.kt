package com.denchic45.studiversity.ui.admindashboard

import com.denchic45.studiversity.ui.search.SearchableComponent
import java.util.UUID

interface AdminSearchableComponent<T> {

    val searchableComponent: SearchableComponent<T>

    fun onSelect(item: T)

    fun onAddClick()

    fun onEditClick(id: UUID)

    fun onRemoveClick(id: UUID)
}