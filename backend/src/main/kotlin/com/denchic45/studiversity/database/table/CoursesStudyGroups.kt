package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object CoursesStudyGroups : Table("course_study_group") {
    val courseId = reference("course_id", Courses.id, onDelete = ReferenceOption.CASCADE)
    val studyGroupId = reference(
        "study_group_id",
        StudyGroups.id,
        onDelete = ReferenceOption.CASCADE
    )
    val enrollmentId = reference(
        "enrollment_id",
        Enrollments.id,
        onDelete = ReferenceOption.CASCADE
    )
}