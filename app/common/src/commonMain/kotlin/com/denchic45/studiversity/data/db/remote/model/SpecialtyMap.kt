package com.denchic45.studiversity.data.db.remote.model

import com.denchic45.studiversity.util.FireMap

data class SpecialtyMap(private val map:FireMap):FireMap by map {
    val id: String by map
    val name: String by map
}