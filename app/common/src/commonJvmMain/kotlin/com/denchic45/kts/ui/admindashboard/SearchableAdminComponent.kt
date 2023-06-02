package com.denchic45.kts.ui.admindashboard

import com.denchic45.kts.ui.chooser.ChooserComponent
import java.util.UUID

interface SearchableAdminComponent<T> {

    val chooserComponent: ChooserComponent<T>

    fun onSelect(item:T)

    fun onAddClick()

    fun onEditClick(id: UUID)
}