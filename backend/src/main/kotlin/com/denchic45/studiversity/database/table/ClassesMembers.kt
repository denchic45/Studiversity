package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ClassesMembers : Table("class_member") {
    val classId = reference("class_id", Classes, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val memberId = reference("member_id", Users, ReferenceOption.CASCADE, ReferenceOption.CASCADE)

    init {
        uniqueIndex("class_member_un", classId, memberId)
    }
}

//class ClassMemberDao(id: EntityID<Long>) : LongEntity(id) {
//    companion object : LongEntityClass<ClassMemberDao>(ClassesMembers)
//
//    var `class` by ClassDao referencedOn ClassesMembers.classId
//    var member by UserDao referencedOn ClassesMembers.memberId
//
//}