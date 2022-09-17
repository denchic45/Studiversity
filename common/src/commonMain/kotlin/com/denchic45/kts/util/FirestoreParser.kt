package com.denchic45.kts.util

import kotlinx.serialization.json.*

private val props = setOf(
    "arrayValue",
    "bytesValue",
    "booleanValue",
    "doubleValue",
    "geoPointValue",
    "integerValue",
    "mapValue",
    "nullValue",
    "referenceValue",
    "stringValue",
    "timestampValue"
)

fun getFireStoreProp(json: MutableFireMap): String? {
    return json.keys.find { k -> props.contains(k) }
}

//fun parseObjectJson(json: JsonObject): MutableMap<String, JsonElement> {
//    return json.map { it.key to getObjectValue(it) }.toMutableStateMap()
//}

fun getObjectValue(element: JsonElement): JsonElement {
    return when (element) {
        is JsonObject -> {
            return when (val prop = element.keys.find { k -> props.contains(k) }) {
                "stringValue" -> {
                    JsonPrimitive(element[prop]!!.jsonPrimitive.content)
                }
                "mapValue" -> {
                    getObjectValue((element[prop] as JsonObject))
                }
                else -> {
                    JsonObject(
                        element.map {
                            it.key to getObjectValue(it.value)
                        }.toMap()
                    )
                }
            }
        }
        is JsonArray -> {
            JsonArray(element.map {
                getObjectValue(it)
            })
        }
        else -> {
            return element
        }
    }
}


fun getMapValue(map: MutableMap<String, JsonElement>): MutableFireMap {
    return map.map { it.key to getObjectValue2(it.value) }.toMap().toMutableMap()
}

fun getObjectValue2(element: JsonElement): Any? {
    return when (element) {
        is JsonObject -> {
            return when (val prop = element.keys.find { k -> props.contains(k) }) {
                "integerValue" -> element.getValue(prop).jsonPrimitive.content.toInt()
                "doubleValue" -> element.getValue(prop).jsonPrimitive.content.toDouble()
                "stringValue" -> element.getValue(prop).jsonPrimitive.content
                "booleanValue" -> element.getValue(prop).jsonPrimitive.content.toBoolean()
                "timestampValue" -> {
                    Dates.parseRfc3339(element.getValue(prop).jsonPrimitive.content)
                }
                "nullValue" ->  null
                "mapValue" -> {
                    element.getValue(prop).jsonObject.getValue("fields").jsonObject.map {
                        it.key to getObjectValue2(it.value)
                    }.toMap()
                }
                "arrayValue" -> {
                    element.getValue(prop).jsonObject.getValue("values").jsonArray.map {
                        getObjectValue2(it)
                    }
                }
                else -> {
                    element.map { it.key to getObjectValue2(it.value) }.toMap()
                }
            }
        }
        is JsonArray -> {
            element.map { getObjectValue2(it) }
        }
        else -> return element
    }
}

fun <T> parseDocuments(element: JsonElement,factory: (MutableFireMap) -> T):List<T> {
    return parseDocuments(element).map(factory)
}

fun parseDocuments(element: JsonElement): List<MutableFireMap> {
    return element.jsonArray.map { parseDocument(it.jsonObject.getValue("document")) }
}

fun parseDocument(element: JsonElement): MutableFireMap {
    return getObjectValue2(element.jsonObject.getValue("fields")) as MutableFireMap
}

fun <T> parseDocument(element: JsonElement,factory: (MutableFireMap) -> T):T {
    return factory(parseDocument(element))
}

//const getFireStoreProp = value => {
//    const props = { 'arrayValue': 1, 'bytesValue': 1, 'booleanValue': 1, 'doubleValue': 1, 'geoPointValue': 1, 'integerValue': 1, 'mapValue': 1, 'nullValue': 1, 'referenceValue': 1, 'stringValue': 1, 'timestampValue': 1 }
//    return Object.keys(value).find(k => props [k] === 1)
//}
//
//export const FireStoreParser = value => {
//    const prop = getFireStoreProp (value)
//    if (prop === 'doubleValue' || prop === 'integerValue') {
//        value = Number(value[prop])
//    } else if (prop === 'arrayValue') {
//        value = (value[prop] && value[prop].values || []).map(v => FireStoreParser (v))
//    } else if (prop === 'mapValue') {
//        value = FireStoreParser(value[prop] && value[prop].fields || {})
//    } else if (prop === 'geoPointValue') {
//        value = { latitude: 0, longitude: 0, ...value[prop] }
//    } else if (prop) {
//        value = value[prop]
//    } else if (typeof value === 'object') {
//        Object.keys(value).forEach(k => value [k] = FireStoreParser(value[k]))
//    }
//    return value
//}
//export default FireStoreParser