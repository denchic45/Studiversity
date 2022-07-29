package com.denchic45.kts.util

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

fun DocumentSnapshot.toMap(): FireMap = data!!.run {
    findAndReplaceTimestamp()
    withDefault { null }
}

private fun MutableFireMap.findAndReplaceTimestamp() {
    filterValues { it is Timestamp }
        .forEach { (key, value) ->
            put(key, (value as Timestamp).toDate())
        }

    values
        .filterIsInstance(MutableMap::class.java)
        .forEach { (it as MutableFireMap).findAndReplaceTimestamp() }
}

fun QuerySnapshot.toMaps() = documents.map { it.toMap() }

fun <T> DocumentSnapshot.toMap(factory: (FireMap) -> T) = factory(toMap())

fun <T> QuerySnapshot.toMaps(factory: (FireMap) -> T) = documents.map { factory(it.toMap()) }