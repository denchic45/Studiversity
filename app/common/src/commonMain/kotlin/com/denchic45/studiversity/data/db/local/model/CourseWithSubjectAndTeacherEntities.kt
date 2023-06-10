package com.denchic45.studiversity.data.db.local.model

import com.denchic45.studiversity.CourseEntity
import com.denchic45.studiversity.SubjectEntity
import com.denchic45.studiversity.UserEntity

class CourseWithSubjectAndTeacherEntities(
    var courseEntity: CourseEntity,
    var subjectEntity: SubjectEntity,
    var teacherEntity: UserEntity
)