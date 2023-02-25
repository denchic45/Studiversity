//package com.denchic45.kts.data.mapper
//
//import com.denchic45.kts.SubmissionEntity
//import com.denchic45.kts.data.db.local.model.SubmissionWithStudentEntities
//import com.denchic45.kts.data.db.remote.model.SubmissionMap
//import com.denchic45.kts.data.domain.model.Attachment
//import com.denchic45.kts.data.domain.model.TaskStatus
//import com.denchic45.kts.data.domain.model.UserRole
//import com.denchic45.kts.domain.model.Task
//import com.denchic45.kts.domain.model.User
//import com.denchic45.stuiversity.util.toToLocalDateTime
//import java.util.*
//
//fun SubmissionMap.mapToEntity() = SubmissionEntity(
//    submission_id = id,
//    student_id = studentId,
//    content_id = contentId,
//    course_id = courseId,
//    status = status,
//    text = text,
//    attachments = attachments,
//    teacher_id = teacherId,
//    cause = cause,
//    grade = grade,
//    graded_date = gradedDate?.time,
//    rejectd_date = rejectedDate?.time,
//    submitted_date = submittedDate?.time,
//    timestamp = timestamp.time
//)
//
//fun Task.Submission.toMap(courseId: String, attachmentUrls: List<String>) = mapOf(
//    "id" to id,
//    "studentId" to student.id,
//    "contentId" to contentId,
//    "courseId" to courseId,
//    "status" to domainToStatus().name,
//    "text" to content.text,
//    "attachments" to attachmentUrls,
////    "submittedDate" to (status as Task.SubmissionStatus.Submitted)
////        .submittedDate.toDate(),
//)
//
////val id: String by map
////val studentId: String by map
////val contentId: String by map
////val courseId: String by map
////val status: String by map
////val text: String by map
////val attachments: List<String> by map
////val teacherId: String by map
////val grade: Int by map
////val gradedDate: Date by map
////val timestamp: Date by map
////val rejectedDate: Date by map
////val cause: String by map
////val comments: List<FireMap> by mapListOrEmpty()
////val submittedDate: Date? by mapOrNull()
//
//fun SubmissionWithStudentEntities.toDomain(attachments: List<Attachment>) =
//    Task.Submission(
//        contentId = submissionEntity.content_id,
//        student = studentEntity.run {
//            User(
//                id = user_id,
//                firstName = first_name,
//                surname = surname,
//                patronymic = patronymic,
//                groupId = user_group_id,
//                photoUrl = photo_url,
//                role = UserRole.valueOf(role),
//                email = email,
//                timestamp = Date(timestamp),
//                gender = gender,
//                generatedAvatar = generated_avatar,
//                admin = admin
//            )
//        },
//        content = Task.Submission.Content(
//            text = submissionEntity.text!!,
//            attachments = attachments
//        ),
//        status = when (TaskStatus.valueOf(submissionEntity.status)) {
//            TaskStatus.NOT_SUBMITTED -> {
//                Task.SubmissionStatus.NotSubmitted
//            }
//            TaskStatus.SUBMITTED -> {
//                Task.SubmissionStatus.Submitted(
//                    submittedDate = submissionEntity.submitted_date!!.toToLocalDateTime()
//                )
//            }
//            TaskStatus.GRADED -> {
//                Task.SubmissionStatus.Graded(
//                    teacher = studentEntity.run {
//                        User(
//                            id = user_id,
//                            firstName = first_name,
//                            surname = surname,
//                            patronymic = patronymic,
//                            groupId = user_group_id,
//                            photoUrl = photo_url,
//                            role = UserRole.valueOf(role),
//                            email = email,
//                            timestamp = Date(timestamp),
//                            gender = gender,
//                            generatedAvatar = generated_avatar,
//                            admin = admin
//                        )
//                    },
//                    grade = submissionEntity.grade!!.toInt(),
//                    gradedDate = submissionEntity.graded_date!!.toToLocalDateTime()
//                )
//            }
//            TaskStatus.REJECTED -> {
//                Task.SubmissionStatus.Rejected(
//                    teacher = studentEntity.run {
//                        User(
//                            id = user_id,
//                            firstName = first_name,
//                            surname = surname,
//                            patronymic = patronymic,
//                            groupId = user_group_id,
//                            photoUrl = photo_url,
//                            role = UserRole.valueOf(role),
//                            email = email,
//                            timestamp = Date(timestamp),
//                            gender = gender,
//                            generatedAvatar = generated_avatar,
//                            admin = admin
//                        )
//                    },
//                    cause = submissionEntity.cause!!,
//                    rejectedDate = submissionEntity.rejectd_date!!.toToLocalDateTime()
//                )
//            }
//        },
//        contentUpdateDate = submissionEntity.timestamp.toToLocalDateTime(),
//        id = submissionEntity.submission_id
//    )
//
//fun Task.Submission.domainToStatus(): TaskStatus = when (status) {
//    is Task.SubmissionStatus.NotSubmitted -> TaskStatus.NOT_SUBMITTED
//    is Task.SubmissionStatus.Submitted -> TaskStatus.SUBMITTED
//    is Task.SubmissionStatus.Graded -> TaskStatus.GRADED
//    is Task.SubmissionStatus.Rejected -> TaskStatus.REJECTED
//}
