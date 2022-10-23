package com.denchic45.kts.util

import kotlin.reflect.KProperty

fun <V> FireMap.mapOrNull() = MapValueOrNullDelegate<V?>(this)

fun <V> MutableFireMap.mapOrNull() = MutableMapValueOrNullDelegate<V?>(this)

fun <V> FireMap.mapListOrEmpty() = MapValueListOrEmptyDelegate<V>(this)

fun <V> MutableFireMap.mapListOrEmpty() = MutableMapValueListOrEmptyDelegate<V>(this)

fun <K, V> FireMap.mapNestedMapOrEmpty() = MapValueMapOrEmptyDelegate<K, V>(this)

fun <V> FireMap.mapOrDefault(defaultValue: V) = MapValueOrDefaultDelegate(this, defaultValue)

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

fun <V, V2> FireMap.mapCast(transform: (V) -> V2) = MapValueCastDelegate(this, transform)

class MapValueCastDelegate<V, V2>(private val map: FireMap, private val transform: (V) -> V2) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any, property: KProperty<*>): V2 =
        transform(map[property.name] as V)
}

class MapValueListOrEmptyDelegate<V>(private val map: FireMap) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<V> =
        map[property.name]?.let { it as List<V> } ?: emptyList()
}

class MutableMapValueListOrEmptyDelegate<V>(private val map: MutableFireMap) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<V> =
        map[property.name]?.let { it as List<V> } ?: emptyList()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: List<V>) {
        map[property.name] = value
    }
}

class MapValueMapOrEmptyDelegate<K, V>(private val map: FireMap) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Map<K, V> =
        map[property.name]?.let { it as Map<K, V> } ?: emptyMap()
}

class MapValueOrDefaultDelegate<V>(private val map: FireMap, private val defaultValue: V) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): V =
        map.getOrDefault(property.name, defaultValue) as V
}