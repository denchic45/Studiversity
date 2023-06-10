//package com.denchic45.studiversity.domain.model
//
//import com.denchic45.studiversity.data.domain.model.DomainModel
//import java.util.UUID
//
//data class Specialty(
//    override var id: UUID,
//    val name: String
//) : DomainModel {
//
//    override fun copy(): Specialty {
//        return Specialty(id, name)
//    }
//
//
//    companion object {
//        fun createEmpty(): Specialty {
//            return Specialty()
//        }
//    }
//}