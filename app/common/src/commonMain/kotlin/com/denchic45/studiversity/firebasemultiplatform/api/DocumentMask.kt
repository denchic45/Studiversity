package com.denchic45.studiversity.firebasemultiplatform.api

import kotlinx.serialization.Serializable

@Serializable
data class DocumentMask(val fieldPaths: List<String>)