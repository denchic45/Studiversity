package com.denchic45.kts.data.remote.model

import com.denchic45.kts.util.FireMap

class SpecialtyMap(override val map:FireMap):MapWrapper {
    val id: String by map
    val name: String by map
}