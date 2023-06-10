package com.denchic45.studiversity.data.remote.model

import com.denchic45.studiversity.data.domain.model.DocModel
import com.denchic45.studiversity.util.SearchKeysGenerator

class SubjectDoc(
    var id: String,
    var name: String,
    var iconUrl: String,
    var colorName: String,
) : DocModel {
    private constructor() : this(
        "",
        "",
        "",
        ""
    )

    val searchKeys: List<String>
        get() = SearchKeysGenerator().generateKeys(name, String::isNotEmpty)

    companion object {
        fun createEmpty(): SubjectDoc = SubjectDoc()
    }
}