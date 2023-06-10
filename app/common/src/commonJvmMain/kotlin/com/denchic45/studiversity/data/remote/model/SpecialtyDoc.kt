package com.denchic45.studiversity.data.remote.model

import com.denchic45.studiversity.data.domain.model.DocModel
import com.denchic45.studiversity.util.SearchKeysGenerator

data class SpecialtyDoc(
    var id: String,
    var name: String,
) : DocModel {

    val searchKeys: List<String>
        get() = SearchKeysGenerator().generateKeys(name) { predicate: String -> predicate.length > 2 }

    private constructor() : this("", "")

    companion object {
        fun createEmpty() = SpecialtyDoc()
    }
}