package com.denchic45.studiversity.firebasemultiplatform.api

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val structuredQuery: StructuredQuery,
    val transaction: String? = null,
    // TODO Add
    //  "newTransaction": {
    //    object (TransactionOptions)
    //  },
    //  "readTime": string
    //  // End of list of possible types for union field consistency_selector.
)
