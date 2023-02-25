package com.denchic45.kts.data.mapper

import com.denchic45.kts.CourseEntity
import com.denchic45.kts.CourseWithSubjectEntity
import com.denchic45.kts.data.db.remote.model.CourseMap
import com.denchic45.kts.domain.model.*
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.util.toUUID
import java.util.*
import kotlin.collections.List

fun CourseResponse.toCourseEntity() = CourseEntity(
    course_id = id.toString(),
    name = name,
    subject_id = subject?.id.toString(),
    archived = archived
)

fun List<CourseResponse>.toCourseEntities() = map(CourseResponse::toCourseEntity)

fun CourseWithSubjectEntity.toCourseResponse() = CourseResponse(
    id = course_id.toUUID(),
    name = name,
    subject = subject_id?.toUUID()?.let {
        SubjectResponse(
            id = it,
            name = subject_name!!,
            shortname = subject_shortname!!,
            iconName = icon_name!!
        )
    } ,
    archived = archived
)

fun List<CourseWithSubjectEntity>.toCourseEntities() = map(CourseWithSubjectEntity::toCourseResponse)

//fun List<CourseMap>.mapsToCourseHeaderDomains() = map { it.toCourseHeader() }

//fun List<GetCourseWithSubjectWithTeacherAndGroupsById>.entityToCourseDomain() = Course(
//    id = first().course_id,
//    name = first().name,
//    subject = first().run {
//        Subject(
//            id = subject_id,
//            name = name,
//            iconName = icon_name
//        )
//    },
//    teacher = first().run {
//        User(
//            id = teacher_id,
//            firstName = first_name,
//            surname = surname,
//            patronymic = patronymic,
//            groupId = user_group_id,
//            role = UserRole.valueOf(role),
//            email = email,
//            photoUrl = photo_url,
//            timestamp = Date(timestamp),
//            gender = gender,
//            generatedAvatar = generated_avatar,
//            admin = admin
//        )
//    },
//    groupHeaders = map {
//        GroupHeader(
//            id = it.group_id,
//            name = it.group_name,
//            specialtyId = it.specialty_id
//        )
//    }
//)

//fun CourseWithSubjectAndTeacherEntities.entityToCourseHeaderDomain() = CourseHeader(
//    id = courseEntity.course_id,
//    name = courseEntity.name,
//    subject = subjectEntity.entityToSubjectDomain(),
//    teacher = teacherEntity.toUserDomain()
//)

//fun List<CourseWithSubjectAndTeacherEntities>.entitiesToCourseHeaders() =
//    map { it.entityToCourseHeaderDomain() }

//fun GetCourseWithSubjectAndTeacherByTeacherId.entityToUserDomain() = CourseHeader(
//    id = course_id,
//    name = name,
//    subject = Subject(
//        id = subject_id,
//        name = name,
//        iconName = icon_name
//    ),
//    User(
//        id = teacher_id,
//        firstName = first_name,
//        surname = surname,
//        patronymic = patronymic,
//        groupId = user_group_id,
//        role = UserRole.valueOf(role),
//        email = email,
//        photoUrl = photo_url,
//        timestamp = Date(timestamp),
//        gender = gender,
//        generatedAvatar = generated_avatar,
//        admin = admin
//    )
//)

//fun List<GetCourseWithSubjectAndTeacherByTeacherId>.entitiesToDomains() =
//    map { it.entityToUserDomain() }

//fun Course.toCourseMap() = mapOf(
//    "id" to id,
//    "name" to name,
//    "subject" to subject.domainToMap(),
//    "teacher" to teacher.domainToUserMap(),
//    "groupIds" to groupHeaders.map { it.id }
//)