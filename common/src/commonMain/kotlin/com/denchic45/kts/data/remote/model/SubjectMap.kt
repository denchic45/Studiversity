package com.denchic45.kts.data.remote.model

import com.denchic45.kts.util.FireMap

class SubjectMap(map: FireMap) {
    val id: String by map
    val name: String by map
    val iconUrl: String by map
    val colorName: String by map
}