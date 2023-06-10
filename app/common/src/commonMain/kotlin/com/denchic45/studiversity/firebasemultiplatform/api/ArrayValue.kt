package com.denchic45.studiversity.firebasemultiplatform.api

import kotlinx.serialization.Serializable

@Serializable
data class ArrayValue(val values: List<Value>)
