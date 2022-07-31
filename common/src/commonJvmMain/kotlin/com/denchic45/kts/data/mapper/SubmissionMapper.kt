package com.denchic45.kts.data.mapper

import com.denchic45.kts.SubmissionEntity
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.local.model.SubmissionWithStudentEntities
import com.denchic45.kts.data.remote.model.SubmissionMap
import com.denchic45.kts.domain.model.Attachment
import com.denchic45.kts.domain.model.Task
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.util.toToLocalDateTime
import java.util.*

fun SubmissionMap.domainToEntity() = SubmissionEntity(
    submission_id = id,
    student_id = studentId,
    content_id = contentId,
    course_id = courseId,
    status = status,
    text = text,
    attachments = attachments,
    teacher_id = teacherId,
    cause = cause,
    grade = grade.toLong(),
    graded_date = gradedDate.time,
    rejectd_date = rejectedDate.time,
    submitted_date = submittedDate.time,
    timestamp = timestamp.time
)

fun SubmissionWithStudentEntities.entityToUserDomain(attachments: List<Attachment>) =
    Task.Submission(
        contentId = submissionEntity.content_id,
        student = studentEntity.run {
            User(
                id = user_id,
                firstName = first_name,
                surname = surname,
                patronymic = patronymic,
                groupId = user_group_id,
                photoUrl = photo_url,
                role = UserRole.valueOf(role),
                email = email,
                timestamp = Date(timestamp),
                gender = gender,
                generatedAvatar = generated_avatar,
                admin = admin
            )
        },
        content = Task.Submission.Content(
            text = submissionEntity.text,
            attachments = attachments
        ),
        status = when (Task.Submission.Status.valueOf(submissionEntity.status)) {
            Task.Submission.Status.NOT_SUBMITTED -> {
                Task.SubmissionStatus.NotSubmitted
            }
            Task.Submission.Status.SUBMITTED -> {
                Task.SubmissionStatus.Submitted(
                    submittedDate = submissionEntity.submitted_date.toToLocalDateTime()
                )
            }
            Task.Submission.Status.GRADED -> {
                Task.SubmissionStatus.Graded(
                    teacher = studentEntity.run {
                        User(
                            id = user_id,
                            firstName = first_name,
                            surname = surname,
                            patronymic = patronymic,
                            groupId = user_group_id,
                            photoUrl = photo_url,
                            role = UserRole.valueOf(role),
                            email = email,
                            timestamp = Date(timestamp),
                            gender = gender,
                            generatedAvatar = generated_avatar,
                            admin = admin
                        )
                    },
                    grade = submissionEntity.grade.toInt(),
                    gradedDate = submissionEntity.graded_date.toToLocalDateTime()
                )
            }
            Task.Submission.Status.REJECTED -> {
                Task.SubmissionStatus.Rejected(
                    teacher = studentEntity.run {
                        User(
                            id = user_id,
                            firstName = first_name,
                            surname = surname,
                            patronymic = patronymic,
                            groupId = user_group_id,
                            photoUrl = photo_url,
                            role = UserRole.valueOf(role),
                            email = email,
                            timestamp = Date(timestamp),
                            gender = gender,
                            generatedAvatar = generated_avatar,
                            admin = admin
                        )
                    },
                    cause = submissionEntity.cause,
                    rejectedDate = submissionEntity.rejectd_date.toToLocalDateTime()
                )
            }
        },
        contentUpdateDate = submissionEntity.timestamp.toToLocalDateTime()
    )

fun Task.Submission.domainToStatus(): Task.Submission.Status = when (status) {
    is Task.SubmissionStatus.NotSubmitted -> Task.Submission.Status.NOT_SUBMITTED
    is Task.SubmissionStatus.Submitted -> Task.Submission.Status.SUBMITTED
    is Task.SubmissionStatus.Graded -> Task.Submission.Status.GRADED
    is Task.SubmissionStatus.Rejected -> Task.Submission.Status.REJECTED
}
