//package com.denchic45.studiversity.domain.model
//
//import com.denchic45.studiversity.data.domain.model.DomainModel
//import java.util.*
//
//data class Group constructor(
//    override var id: UUID,
//    var name: String,
//    var course: Int,
//    var specialty: Specialty,
//    var curator: User
//) : DomainModel {
//
//    var timestamp: Date? = null
//}
//
//data class GroupHeader(
//    override var id: UUID,
//    val name: String,
//    val specialtyId: String
//) : DomainModel {
//
//    companion object {
//        fun createEmpty(): GroupHeader = GroupHeader()
//    }
//}
