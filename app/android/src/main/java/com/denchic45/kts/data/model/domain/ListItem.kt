package com.denchic45.kts.data.model.domain

import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.ui.model.UiColor
import com.denchic45.kts.ui.model.UiImage
import com.denchic45.kts.ui.model.UiText
import com.google.gson.annotations.SerializedName
import java.util.UUID


data class ListItem(
    @SerializedName("_id")
    override var id: UUID = UUID.randomUUID(),
    var title: String = "",
    var icon: UiImage? = null,
    var color: UiColor? = null,
    var content: Any? = null,
    val enable: Boolean = true,
    var type: Int = 0
) : DomainModel {

    companion object {
        val emptyIcon = UiText.IdText(0)

        val emptyColor = UiText.IdText(0)
    }

    fun hasIcon(): Boolean = icon != null
}