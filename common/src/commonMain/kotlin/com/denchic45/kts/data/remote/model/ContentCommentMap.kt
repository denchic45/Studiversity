package com.denchic45.kts.data.remote.model

import com.denchic45.kts.util.FireMap
import java.util.*

class ContentCommentMap(
    val map: FireMap
) {
    val id: String by map
    val contentId: String by map
    val content: String by map
    val authorId: String by map
    val createdDate: Date by map
}
