package com.denchic45.firebasemultiplatform.api

import kotlinx.serialization.Serializable

@Serializable
data class Cursor(val values: List<Value>, val before: Boolean)
