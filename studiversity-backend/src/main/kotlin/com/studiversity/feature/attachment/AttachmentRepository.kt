package com.studiversity.feature.attachment

import com.studiversity.database.exists
import com.studiversity.database.table.*
import com.studiversity.supabase.deleteRecursive
import com.denchic45.stuiversity.api.course.element.model.*
import io.github.jan.supabase.storage.BucketApi
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import java.util.*

class AttachmentRepository(private val bucket: BucketApi) {

    suspend fun addCourseElementFileAttachment(
        elementId: UUID,
        courseId: UUID,
        file: CreateFileRequest
    ): FileAttachmentHeader = addFileAttachment(
        file = file,
        path = "courses/$courseId/elements/$elementId/${file.name}",
        ownerId = elementId,
        ownerType = AttachmentOwner.COURSE_ELEMENT,
        insertReferences = { addAttachmentCourseElementReference(it, elementId) }
    )

    fun addCourseElementLinkAttachment(elementId: UUID, link: CreateLinkRequest): LinkAttachmentHeader {
        return addLinkAttachment(
            link = link,
            ownerId = elementId,
            ownerType = AttachmentOwner.COURSE_ELEMENT,
            insertReferences = { addAttachmentCourseElementReference(it, elementId) }
        )
    }

    suspend fun addSubmissionFileAttachment(
        submissionId: UUID,
        courseId: UUID,
        workId: UUID,
        createFileRequest: CreateFileRequest
    ): FileAttachmentHeader = addFileAttachment(
        file = createFileRequest,
        path = "courses/$courseId/elements/$workId/submissions/$submissionId/${createFileRequest.name}",
        ownerId = submissionId,
        ownerType = AttachmentOwner.SUBMISSION,
        insertReferences = { addAttachmentSubmissionReference(it, submissionId) })

    fun addSubmissionLinkAttachment(submissionId: UUID, attachment: CreateLinkRequest): LinkAttachmentHeader {
        return addLinkAttachment(
            link = attachment,
            ownerId = submissionId,
            ownerType = AttachmentOwner.SUBMISSION,
            insertReferences = { addAttachmentSubmissionReference(it, submissionId) }
        )
    }

    private suspend fun addFileAttachment(
        file: CreateFileRequest,
        path: String,
        ownerId: UUID,
        ownerType: AttachmentOwner,
        insertReferences: (dao: AttachmentDao) -> Unit
    ): FileAttachmentHeader = AttachmentDao.new {
        this.name = file.name
        this.type = AttachmentType.FILE
        this.path = path
        this.ownerId = ownerId
        this.ownerType = ownerType
    }.also { dao ->
        insertReferences(dao)
        bucket.upload(path, file.bytes)
    }.toFileAttachmentHeader()

    private fun addLinkAttachment(
        link: CreateLinkRequest,
        ownerId: UUID,
        ownerType: AttachmentOwner,
        insertReferences: (dao: AttachmentDao) -> Unit
    ): LinkAttachmentHeader {
        return AttachmentDao.new {
            this.name = "Link name" // TODO ставить реальное название
            this.type = AttachmentType.LINK
            this.url = link.url
            this.ownerId = ownerId
            this.ownerType = ownerType
        }.also(insertReferences).toLinkAttachmentHeader()
    }


    private fun addAttachmentSubmissionReference(dao: AttachmentDao, submissionId: UUID) {
        AttachmentsSubmissions.insert {
            it[attachmentId] = dao.id.value
            it[AttachmentsSubmissions.submissionId] = submissionId
        }
    }

    private fun addAttachmentCourseElementReference(dao: AttachmentDao, elementId: UUID) {
        AttachmentsCourseElements.insert {
            it[attachmentId] = dao.id.value
            it[courseElementId] = elementId
        }
    }

    fun findAttachmentsBySubmissionId(submissionId: UUID): List<AttachmentHeader> {
        return Attachments.innerJoin(AttachmentsSubmissions, { Attachments.id }, { attachmentId })
            .select { AttachmentsSubmissions.submissionId eq submissionId }
            .map(ResultRow::toAttachment)
    }

    fun findAttachmentsByCourseElementId(elementId: UUID): List<AttachmentHeader> {
        return Attachments.innerJoin(AttachmentsCourseElements, { Attachments.id }, { attachmentId })
            .select { AttachmentsCourseElements.courseElementId eq elementId }
            .map(ResultRow::toAttachment)
    }

    suspend fun removeBySubmissionId(courseId: UUID, elementId: UUID, submissionId: UUID, attachmentId: UUID): Boolean {
        return removeAttachment(
            ownerId = submissionId,
            attachmentId = attachmentId,
            path = { name -> "courses/$courseId/elements/$elementId/submissions/$submissionId/${name}" },
            removeReference = {
                AttachmentsSubmissions.deleteWhere {
                    AttachmentsSubmissions.submissionId eq submissionId and (AttachmentsSubmissions.attachmentId eq attachmentId)
                }
            }
        )
    }

    suspend fun removeByCourseElementId(courseId: UUID, elementId: UUID, attachmentId: UUID): Boolean {
        return removeAttachment(
            ownerId = elementId,
            attachmentId = attachmentId,
            path = { name -> "courses/$courseId/elements/$elementId/${name}" },
            removeReference = {
                AttachmentsCourseElements.deleteWhere {
                    courseElementId eq elementId and (AttachmentsCourseElements.attachmentId eq attachmentId)
                }
            }
        )
    }

    private suspend fun removeAttachment(
        ownerId: UUID,
        attachmentId: UUID,
        path: (name: String) -> String,
        removeReference: () -> Unit
    ): Boolean {
        val attachmentDao = AttachmentDao.findById(attachmentId) ?: return false
        if (attachmentDao.ownerId == ownerId) {
            remove(attachmentId)
            if (attachmentDao.type == AttachmentType.FILE)
                bucket.delete(path(attachmentDao.name))
        } else {
            removeReference()
        }
        return true
    }

    suspend fun removeAllByCourseId(courseId: UUID) {
        val elementIds = CourseElements.slice(CourseElements.id)
            .select(CourseElements.courseId eq courseId)
            .map { it[CourseElements.id].value }

        val submissionIds = Submissions.slice(Submissions.id)
            .select(Submissions.courseWorkId inList elementIds)
            .map { it[Submissions.id].value }

        removeByOwnerIds(elementIds + submissionIds)

        bucket.deleteRecursive("courses/$courseId")
    }

    suspend fun removeAllByCourseWorkId(courseId: UUID, workId: UUID) {
        removeAllByCourseElementId(courseId, workId)
        removeByOwnerIds(
            Submissions.slice(Submissions.id)
                .select(Submissions.courseWorkId eq workId)
                .map { it[Submissions.id].value }
        )
    }

    private suspend fun removeAllByCourseElementId(courseId: UUID, elementId: UUID) {
        removeByOwnerId(elementId)
        bucket.deleteRecursive("courses/$courseId/elements/$elementId")
    }

    private fun remove(attachmentId: UUID) {
        Attachments.deleteWhere { Attachments.id eq attachmentId }
    }

    private fun removeByOwnerId(ownerId: UUID) {
        Attachments.deleteWhere { Attachments.ownerId eq ownerId }
    }

    private fun removeByOwnerIds(ownerIds: List<UUID>) {
        Attachments.deleteWhere { ownerId inList ownerIds }
    }

    suspend fun findAttachmentByIdAndCourseElementId(courseId: UUID, workId: UUID, attachmentId: UUID): Attachment? {
        return if (CourseElementDao.existByCourseId(workId, courseId)) {
            val attachment = Attachments.innerJoin(AttachmentsCourseElements,
                { Attachments.id },
                { AttachmentsCourseElements.attachmentId })
                .innerJoin(CourseElements, { AttachmentsCourseElements.courseElementId }, { CourseElements.id })
                .select(
                    AttachmentsCourseElements.attachmentId eq attachmentId
                            and (AttachmentsCourseElements.courseElementId eq workId)
                ).singleOrNull()
            toAttachment(attachment)
        } else null
    }

    suspend fun findAttachmentByIdAndSubmissionId(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
        attachmentId: UUID
    ): Attachment? {
        val existSubmissionByElementId = Submissions.exists {
            Submissions.id eq submissionId and (Submissions.courseWorkId eq workId)
        }
        return if (existSubmissionByElementId && CourseElementDao.existByCourseId(workId, courseId)) {
            val attachment = Attachments.innerJoin(AttachmentsSubmissions,
                { Attachments.id },
                { AttachmentsSubmissions.attachmentId })
                .innerJoin(Submissions, { AttachmentsSubmissions.submissionId }, { Submissions.id })
                .select(
                    AttachmentsSubmissions.attachmentId eq attachmentId
                            and (AttachmentsSubmissions.submissionId eq submissionId)
                ).singleOrNull()
            toAttachment(attachment)
        } else null
    }

    private suspend fun toAttachment(attachment: ResultRow?): Attachment? {
        return attachment?.let {
            when (attachment[Attachments.type]) {
                AttachmentType.FILE -> FileAttachment(
                    bucket.downloadPublic(attachment[Attachments.path]!!),
                    attachment[Attachments.name]
                )

                AttachmentType.LINK -> attachment.toLink()
            }
        }
    }
}