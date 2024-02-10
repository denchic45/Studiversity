package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.sql.Table

object UserEnrollments : Table("user_enrollments") {
    val enrollmentId = reference("enrollment_id", Enrollments.id)
    val userId = reference("user_id", Users)
}