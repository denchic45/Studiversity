package com.denchic45.kts.util

import com.denchic45.kts.data.remote.model.MapWrapper
import com.denchic45.kts.data.remote.model.MutableMapWrapper
import com.denchic45.kts.data.remote.model.UserMap
import kotlin.reflect.KProperty

fun <V> MapWrapper.mapOrNull() = MapValueOrNullDelegate<V?>(map)

fun <V> MutableMapWrapper.mapOrNull() = MutableMapValueOrNullDelegate<V?>(map)

open class MapValueOrNullDelegate<V>(private val map: FireMap) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): V? = map[property.name] as V
}

class MutableMapValueOrNullDelegate<V>(private val map: MutableFireMap) :
    MapValueOrNullDelegate<V>(map) {
    @Suppress("UNCHECKED_CAST")
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        map[property.name] = value as Any?
    }
}

fun <V, V2> MapWrapper.mapCast(transform: (V) -> V2) = MapValueCastDelegate(map, transform)

class MapValueCastDelegate<V, V2>(private val map: FireMap, private val transform: (V) -> V2) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any, property: KProperty<*>): V2 =
        transform(map[property.name] as V)
}