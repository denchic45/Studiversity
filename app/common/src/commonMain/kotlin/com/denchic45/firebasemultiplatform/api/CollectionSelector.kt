package com.denchic45.firebasemultiplatform.api

import kotlinx.serialization.Serializable


@Serializable
data class CollectionSelector(val collectionId: String, val allDescendants:Boolean = false)
