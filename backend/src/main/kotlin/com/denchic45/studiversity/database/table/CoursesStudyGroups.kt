package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.sql.Table

object CoursesStudyGroups : Table("course_study_group") {
    val courseId = reference("course_id", Courses.id)
    val studyGroupId = reference("study_group_id", StudyGroups.id)
    val enrollmentId = reference("enrollment_id", Enrollments.id)
}