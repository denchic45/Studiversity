package com.denchic45.studiversity.ui.admindashboard

import com.denchic45.studiversity.ui.search.ChooserComponent
import java.util.UUID

interface SearchableAdminComponent<T> {

    val chooserComponent: ChooserComponent<T>

    fun onSelect(item: T)

    fun onAddClick()

    fun onEditClick(id: UUID)

    fun onRemoveClick(id: UUID)
}