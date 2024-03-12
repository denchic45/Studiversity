package com.denchic45.studiversity

import com.denchic45.stuiversity.util.OptionalProperty
import com.denchic45.stuiversity.util.optPropertyOf

class FieldEditor(private val fields: Map<String, Field<*>>) {

    fun hasChanges() = fields.any { it.value.hasChanged() }

    fun hasChanges(vararg names: String) = fields.any { it.key in names && it.value.hasChanged() }

    fun fieldChanged(name: String) = fields.getValue(name).hasChanged()

    @Suppress("UNCHECKED_CAST")
    fun <T> field(name: String): Field<T> = fields.getValue(name) as Field<T>

    fun <T> getValue(name: String) = field<T>(name)

    fun <T> ifChanged(name: String, block: (T) -> Unit) {
        val value = field<T>(name)
        if (value.hasChanged())
            block(value.currentValue())
    }

    fun <T> updateOldValueBy(name: String, oldValue: T) {
        field<T>(name).oldValue = oldValue
    }

    fun updateOldValues() {
        fields.forEach { (name, field) ->
            updateOldValueBy(name, field.currentValue())
        }
    }
}

class Field<T>(val currentValue: () -> T) {
    var oldValue: T = currentValue()

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