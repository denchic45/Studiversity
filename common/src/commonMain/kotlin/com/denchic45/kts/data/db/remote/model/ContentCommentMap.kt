package com.denchic45.kts.data.db.remote.model

import com.denchic45.kts.util.FireMap
import java.util.*

data class ContentCommentMap(private val map: FireMap) : FireMap by map {
    val id: String by map
    val contentId: String by map
    val content: String by map
    val authorId: String by map
    val createdDate: Date by map
}
