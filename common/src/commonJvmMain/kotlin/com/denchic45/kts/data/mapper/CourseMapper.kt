package com.denchic45.kts.data.mapper

import com.denchic45.kts.CourseEntity
import com.denchic45.kts.GetCourseWithSubjectAndTeacherByTeacherId
import com.denchic45.kts.GetCourseWithSubjectWithTeacherAndGroupsById
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.local.model.CourseWithSubjectAndTeacherEntities
import com.denchic45.kts.data.remote.model.CourseMap
import com.denchic45.kts.data.remote.model.SubjectMap
import com.denchic45.kts.data.remote.model.UserMap
import com.denchic45.kts.domain.model.*
import java.util.*

fun CourseMap.mapToEntity() = CourseEntity(
    course_id = id,
    name = name,
    subject_id = subject["id"] as String,
    teacher_id = teacher["id"] as String
)

fun CourseMap.toCourseHeader() = CourseHeader(
    id = id,
    name = name,
    subject = SubjectMap(subject).mapToSubjectDomain(),
    teacher = UserMap(teacher).mapToUser()
)

fun List<CourseMap>.mapsToCourseHeaderDomains() = map { it.toCourseHeader() }

fun List<GetCourseWithSubjectWithTeacherAndGroupsById>.entityToCourseDomain() = Course(
    id = first().course_id,
    name = first().name,
    subject = first().run {
        Subject(
            id = subject_id,
            name = name,
            iconUrl = icon_url,
            colorName = color_name
        )
    },
    teacher = first().run {
        User(
            id = teacher_id,
            firstName = first_name,
            surname = surname,
            patronymic = patronymic,
            groupId = user_group_id,
            role = UserRole.valueOf(role),
            email = email,
            photoUrl = photo_url,
            timestamp = Date(timestamp),
            gender = gender,
            generatedAvatar = generated_avatar,
            admin = admin
        )
    },
    groupHeaders = map {
        GroupHeader(
            id = it.group_id,
            name = it.group_name,
            specialtyId = it.specialty_id
        )
    }
)

fun CourseWithSubjectAndTeacherEntities.entityToCourseHeaderDomain() = CourseHeader(
    id = courseEntity.course_id,
    name = courseEntity.name,
    subject = subjectEntity.entityToSubjectDomain(),
    teacher = teacherEntity.toUserDomain()
)

fun List<CourseWithSubjectAndTeacherEntities>.entitiesToCourseHeaders() = map { it.entityToCourseHeaderDomain() }

fun GetCourseWithSubjectAndTeacherByTeacherId.entityToUserDomain() = CourseHeader(
    id = course_id,
    name = name,
    subject = Subject(
        id = subject_id,
        name = name,
        iconUrl = icon_url,
        colorName = color_name
    ),
    User(
        id = teacher_id,
        firstName = first_name,
        surname = surname,
        patronymic = patronymic,
        groupId = user_group_id,
        role = UserRole.valueOf(role),
        email = email,
        photoUrl = photo_url,
        timestamp = Date(timestamp),
        gender = gender,
        generatedAvatar = generated_avatar,
        admin = admin
    )
)

fun List<GetCourseWithSubjectAndTeacherByTeacherId>.entitiesToDomains() = map { it.entityToUserDomain() }

fun Course.domainToCourseMap() = mapOf(
    "id" to id,
    "name" to name,
    "subject" to subject.domainToMap(),
    "teacher" to teacher.domainToUserMap(),
    "groupIds" to groupHeaders.map { it.id }
)