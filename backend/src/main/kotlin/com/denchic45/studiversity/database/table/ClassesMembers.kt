package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ClassesMembers : Table("class_member") {
    val classId = reference("class_id", Classes, ReferenceOption.CASCADE)
    val memberId = reference("member_id", Users, ReferenceOption.CASCADE)

    init {
        uniqueIndex("class_member_un", classId, memberId)
    }
}