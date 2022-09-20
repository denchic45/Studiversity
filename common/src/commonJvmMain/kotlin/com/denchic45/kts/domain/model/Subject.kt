package com.denchic45.kts.domain.model

import com.denchic45.kts.data.domain.model.DomainModel

data class Subject(
    override var id: String,
    val name: String,
    val iconName: String,
) : DomainModel {

    val isEmpty: Boolean
    get() = id.isEmpty()

    private constructor():this("","","")

    override fun copy(): Subject {
        return Subject(id, name, iconName)
    }

    companion object {

        fun createEmpty(): Subject {
            return Subject()
        }
    }
}