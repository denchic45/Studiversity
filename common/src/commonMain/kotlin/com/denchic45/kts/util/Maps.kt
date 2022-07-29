package com.denchic45.kts.util

import com.denchic45.kts.data.remote.model.MapWrapper
import kotlin.reflect.KProperty

fun <V> MapWrapper.mapOrNull() = MapValueOrNullDelegate<V?>(map)

class MapValueOrNullDelegate<V>(private val map: FireMap) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): V? = map[property.name] as V
}