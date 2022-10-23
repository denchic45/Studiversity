package com.denchic45.firebasemultiplatform.api

@kotlinx.serialization.Serializable
data class Commit(val writes: List<Write>, val transaction: String? = null)
