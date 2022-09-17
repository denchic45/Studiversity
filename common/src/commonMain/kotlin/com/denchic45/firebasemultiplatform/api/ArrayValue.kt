package com.denchic45.firebasemultiplatform.api

import kotlinx.serialization.Serializable

@Serializable
data class ArrayValue(val values: List<Value>)
