package com.denchic45.kts.data.remote.model

import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.MutableFireMap

interface MapWrapper {
    val map: FireMap
}

interface MutableMapWrapper:MapWrapper {
    override val map: MutableFireMap
}