package com.denchic45.kts.data.db.remote.model

import com.denchic45.kts.util.FireMap

data class SubjectMap(private val map: FireMap) : FireMap by map {
    val id: String by map
    val name: String by map
    val iconName: String by map
    val colorName: String by map
}