package com.denchic45.kts

import com.denchic45.stuiversity.util.OptionalProperty
import com.denchic45.stuiversity.util.optPropertyOf

class FieldEditor(private val fields: Map<String, Field<*>>) {

    fun hasChanges() = fields.any { it.value.hasChanged() }

    fun fieldChanged(name: String) = fields.getValue(name).hasChanged()

    @Suppress("UNCHECKED_CAST")
    fun <T> field(name: String): Field<T> = fields.getValue(name) as Field<T>

    fun <T> getValue(name: String) = field<T>(name)

    fun <T> ifChanged(name: String, value: () -> T): OptionalProperty<T> {
        return if (fields.getValue(name).hasChanged())
            optPropertyOf(value())
        else OptionalProperty.NotPresent
    }


    fun <T> updateOldValueBy(name: String, oldValue: T) {
        field<T>(name).oldValue = oldValue
    }
}

class Field<T>(
    var oldValue: T,
    val currentValue: () -> T,
) {
    fun hasChanged() = oldValue != currentValue()
}

fun FieldEditor.updateOldValues(vararg fields: Pair<String, *>) {
    fields.forEach { (name, value) ->
        updateOldValueBy(name, value)
    }
}

fun <T> FieldEditor.getOptProperty(name: String): OptionalProperty<T> {
    val field = field<T>(name)
    return if (field.hasChanged())
        optPropertyOf(field.currentValue())
    else OptionalProperty.NotPresent
}

fun <T, V> FieldEditor.getOptProperty(name: String, map: (T) -> V): OptionalProperty<V> {
    val field = field<T>(name)
    return if (field.hasChanged())
        optPropertyOf(map(field.currentValue()))
    else OptionalProperty.NotPresent
}