package com.denchic45.uivalidator.experimental

fun interface Trigger {
    operator fun invoke(validatable: Validatable)
}