package com.denchic45.kts.data.db.remote.model

import com.denchic45.kts.util.FireMap
import java.util.*

class ContentCommentMap(override val map: FireMap) : MapWrapper {
    val id: String by map
    val contentId: String by map
    val content: String by map
    val authorId: String by map
    val createdDate: Date by map
}
