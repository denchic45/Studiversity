//package com.denchic45.studiversity.domain.model
//
//import com.denchic45.studiversity.data.domain.model.DomainModel
//import com.denchic45.studiversity.util.UUIDS
//
//data class Section(
//    val courseId: String,
//    val name: String,
//    val order: Int,
//    override var id: String = UUIDS.createShort()
//) : DomainModel {
//    companion object {
//        fun createEmpty() = Section("", "", -1, "")
//    }
//}