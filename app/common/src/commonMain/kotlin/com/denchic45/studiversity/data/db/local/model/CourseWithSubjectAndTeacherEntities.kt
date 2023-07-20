package com.denchic45.studiversity.data.db.local.model

import com.denchic45.studiversity.entity.Course
import com.denchic45.studiversity.entity.Subject
import com.denchic45.studiversity.entity.User

class CourseWithSubjectAndTeacherEntities(
    var courseEntity: Course,
    var Subject: Subject,
    var teacherEntity: User
)