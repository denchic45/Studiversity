package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.supabase.deleteRecursive
import com.denchic45.stuiversity.api.course.element.model.*
import io.github.jan.supabase.storage.BucketApi
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import java.util.*

class AttachmentRepository(private val bucket: BucketApi) {

//    suspend fun addCourseElementFileAttachment(
//        elementId: UUID,
//        courseId: UUID,
//        file: CreateFileRequest
//    ): FileAttachmentHeader = addFileAttachment(
//        file = file,
//        path = "courses/$courseId/elements/$elementId/${file.name}",
//        ownerId = elementId,
//        ownerType = AttachmentOwner.COURSE_ELEMENT,
//        insertReferences = { addAttachmentCourseElementReference(it, elementId) }
//    )

//    fun addCourseElementLinkAttachment(elementId: UUID, link: CreateLinkRequest): LinkAttachmentHeader {
//        return addLinkAttachment(
//            link = link,
//            ownerId = elementId,
//            ownerType = AttachmentOwner.COURSE_ELEMENT,
//            insertReferences = { addAttachmentCourseElementReference(it, elementId) }
//        )
//    }

//    suspend fun addSubmissionFileAttachment(
//        submissionId: UUID,
//        courseId: UUID,
//        workId: UUID,
//        createFileRequest: CreateFileRequest
//    ): FileAttachmentHeader = addFileAttachment(
//        file = createFileRequest,
//        path = "courses/$courseId/elements/$workId/submissions/$submissionId/${createFileRequest.name}",
//        ownerId = submissionId,
//        ownerType = AttachmentOwner.SUBMISSION,
//        insertReferences = { addAttachmentSubmissionReference(it, submissionId) })

//    fun addSubmissionLinkAttachment(submissionId: UUID, attachment: CreateLinkRequest): LinkAttachmentHeader {
//        return addLinkAttachment(
//            link = attachment,
//            ownerId = submissionId,
//            ownerType = AttachmentOwner.SUBMISSION,
//            insertReferences = { addAttachmentSubmissionReference(it, submissionId) }
//        )
//    }

    suspend fun addFileAttachment(
        request: CreateFileRequest,
        ownerId: UUID,
//        ownerType: AttachmentOwner
    ): FileAttachmentHeader = AttachmentDao.new {
        this.name = request.name
        this.type = AttachmentType.FILE
//        this.path = getPathBy
        this.ownerId = ownerId
//        this.ownerType = ownerType
    }.also { dao ->
        AttachmentReferenceDao.new {
            this.attachment = dao
            this.consumerId = ownerId
        }
        bucket.upload("attachments/${dao.id.value}", request.bytes)
    }.toFileAttachmentHeader()

//    fun addLinkAttachment(submissionId: UUID, attachment: CreateLinkRequest): LinkAttachmentHeader {
//        return addLinkAttachment(
//            link = attachment,
//            ownerId = submissionId,
//            ownerType = AttachmentOwner.SUBMISSION,
//            insertReferences = { addAttachmentSubmissionReference(it, submissionId) }
//        )
//    }

//    private suspend fun addFileAttachment(
//        file: CreateFileRequest,
//        path: String,
//        ownerId: UUID,
//        ownerType: AttachmentOwner,
//        insertReferences: (dao: AttachmentDao) -> Unit
//    ): FileAttachmentHeader = AttachmentDao.new {
//        this.name = file.name
//        this.type = AttachmentType.FILE
//        this.path = path
//        this.ownerId = ownerId
//        this.ownerType = ownerType
//    }.also { dao ->
//        insertReferences(dao)
//        bucket.upload(path, file.bytes)
//    }.toFileAttachmentHeader()

    fun addLinkAttachment(
        link: CreateLinkRequest,
        ownerId: UUID,
//        ownerType: AttachmentOwner
    ): LinkAttachmentHeader {
        return AttachmentDao.new {
            this.name = "Link name" // TODO ставить реальное название
            this.type = AttachmentType.LINK
            this.url = link.url
            this.ownerId = ownerId
//            this.ownerType = ownerType
        }.also { dao ->
            AttachmentReferenceDao.new {
                this.attachment = dao
                this.consumerId = ownerId
            }
        }.toLinkAttachmentHeader()
    }

    // TODO: В будущем передавать также consumerType? чтобы проверить его наличие, напр:
    //  consumerType может быть SUBMISSION

    private fun addAttachmentReference(attachmentId: UUID, consumerId: UUID): Boolean {
        val attachmentDao = AttachmentDao.findById(attachmentId) ?: return false
        AttachmentReferenceDao.new {
            this.attachment = attachmentDao
            this.consumerId = consumerId
        }
        return true
    }

    suspend fun removeByCourseId(courseId: UUID) {
        CourseElements.select { CourseElements.courseId eq courseId }
            .forEach { removeByCourseElementId(it[CourseElements.id].value) }
    }

    suspend fun removeByCourseElementId(elementId: UUID) {
        Submissions.select { Submissions.courseWorkId eq elementId }.forEach {
            removeByConsumer(it[Submissions.id].value)
        }
        removeByConsumer(elementId)
    }

//    private fun addAttachmentSubmissionReference(dao: AttachmentDao, submissionId: UUID) {
//        AttachmentsSubmissions.insert {
//            it[attachmentId] = dao.id.value
//            it[AttachmentsSubmissions.submissionId] = submissionId
//        }
//    }

//    private fun addAttachmentCourseElementReference(dao: AttachmentDao, elementId: UUID) {
//        AttachmentsCourseElements.insert {
//            it[attachmentId] = dao.id.value
//            it[courseElementId] = elementId
//        }
//    }

//    fun findAttachmentsBySubmissionId(submissionId: UUID): List<AttachmentHeader> {
//        return Attachments.innerJoin(AttachmentsSubmissions, { Attachments.id }, { attachmentId })
//            .select { AttachmentsSubmissions.submissionId eq submissionId }
//            .map(ResultRow::toAttachment)
//    }

//    fun findAttachmentsByCourseElementId(elementId: UUID): List<AttachmentHeader> {
//        return Attachments.innerJoin(AttachmentsCourseElements, { Attachments.id }, { attachmentId })
//            .select { AttachmentsCourseElements.courseElementId eq elementId }
//            .map(ResultRow::toAttachment)
//    }

    suspend fun removeConsumer(attachmentId: UUID, consumerId: UUID): Boolean {
        val attachmentDao = AttachmentDao.findById(attachmentId)
        return attachmentDao?.apply {
            if (attachmentDao.ownerId == consumerId)
                remove(attachmentDao)
            else
                AttachmentReferences.deleteWhere {
                    AttachmentReferences.attachmentId eq attachmentId and (AttachmentReferences.consumerId eq consumerId)
                }
        } != null
    }

    suspend fun removeByConsumer(consumerId: UUID) {
        val attachmentIds = Attachments.innerJoin(AttachmentReferences, { Attachments.id }, { attachmentId }).select {
            AttachmentReferences.consumerId eq consumerId
        }.map { it[Attachments.id].value }

        if (attachmentIds.isNotEmpty())
            bucket.delete(attachmentIds.map { "attachments/$it" })

        Attachments.deleteWhere { Attachments.id inList attachmentIds }
    }

    suspend fun remove(attachmentDao: AttachmentDao) {
        when (attachmentDao.type) {
            AttachmentType.FILE -> {
                bucket.delete("attachments/${attachmentDao.id.value}")
            }

            AttachmentType.LINK -> {

            }
        }
        Attachments.deleteWhere { Attachments.id eq attachmentDao.id }
    }

//    suspend fun removeBySubmissionId(courseId: UUID, elementId: UUID, submissionId: UUID, attachmentId: UUID): Boolean {
//        return removeAttachment(
//            ownerId = submissionId,
//            attachmentId = attachmentId,
//            path = { name -> "courses/$courseId/elements/$elementId/submissions/$submissionId/${name}" },
//            removeReference = {
//                AttachmentsSubmissions.deleteWhere {
//                    AttachmentsSubmissions.submissionId eq submissionId and (AttachmentsSubmissions.attachmentId eq attachmentId)
//                }
//            }
//        )
//    }

//    suspend fun removeByCourseElementId(courseId: UUID, elementId: UUID, attachmentId: UUID): Boolean {
//        return removeAttachment(
//            ownerId = elementId,
//            attachmentId = attachmentId,
//            path = { name -> "courses/$courseId/elements/$elementId/${name}" },
//            removeReference = {
//                AttachmentsCourseElements.deleteWhere {
//                    courseElementId eq elementId and (AttachmentsCourseElements.attachmentId eq attachmentId)
//                }
//            }
//        )
//    }

//    private suspend fun removeAttachment(
//        ownerId: UUID,
//        attachmentId: UUID,
//        path: (name: String) -> String,
//        removeReference: () -> Unit
//    ): Boolean {
//        val attachmentDao = AttachmentDao.findById(attachmentId) ?: return false
//        if (attachmentDao.ownerId == ownerId) {
//            remove(attachmentId)
//            if (attachmentDao.type == AttachmentType.FILE)
//                bucket.delete(path(attachmentDao.name))
//        } else {
//            removeReference()
//        }
//        return true
//    }

    suspend fun removeAttachment(attachmentId: UUID): Boolean {
        return AttachmentDao.findById(attachmentId)?.let { remove(it) } != null
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

//    suspend fun removeAllByCourseWorkId(courseId: UUID, workId: UUID) {
//        removeAllByCourseElementId(courseId, workId)
//        removeByOwnerIds(
//            Submissions.slice(Submissions.id)
//                .select(Submissions.courseWorkId eq workId)
//                .map { it[Submissions.id].value }
//        )
//    }

//    private suspend fun removeAllByCourseElementId(courseId: UUID, elementId: UUID) {
//        removeByOwnerId(elementId)
//        bucket.deleteRecursive("courses/$courseId/elements/$elementId")
//    }

    private fun remove(attachmentId: UUID) {
        Attachments.deleteWhere { Attachments.id eq attachmentId }
    }

    private fun removeByOwnerId(ownerId: UUID) {
        Attachments.deleteWhere { Attachments.ownerId eq ownerId }
    }

    private fun removeByOwnerIds(ownerIds: List<UUID>) {
        Attachments.deleteWhere { ownerId inList ownerIds }
    }

    suspend fun findAttachmentById(attachmentId: UUID): AttachmentResponse? {
        return AttachmentDao.findById(attachmentId)?.toAttachment()
    }

//    suspend fun findAttachmentByIdAndCourseElementId(
//        courseId: UUID,
//        workId: UUID,
//        attachmentId: UUID
//    ): AttachmentResponse? {
//        return if (CourseElementDao.existByCourseId(workId, courseId)) {
//            val attachment = Attachments.innerJoin(AttachmentReferences,
//                { Attachments.id },
//                { AttachmentReferences.attachmentId })
//                .innerJoin(CourseElements, { AttachmentReferences.consumerId }, { CourseElements.id })
//                .select(
//                    AttachmentReferences.attachmentId eq attachmentId
//                            and (AttachmentReferences.consumerId eq workId)
//                ).singleOrNull()
//            toAttachment(attachment)
//        } else null
//    }

    fun findAttachmentsByReferenceId(consumerId: UUID): List<AttachmentHeader> {
        return AttachmentDao.wrapRows(
            Attachments.innerJoin(AttachmentReferences, { Attachments.id }, { attachmentId })
                .select(AttachmentReferences.consumerId eq consumerId)
        ).map { (it).toHeader() }
    }

    private suspend fun AttachmentDao.toAttachment(): AttachmentResponse {
        return when (type) {
            AttachmentType.FILE -> toFileResponse(bucket.downloadPublic("attachments/${id.value}"))
            AttachmentType.LINK -> toLinkResponse()
        }
    }

    fun checkIsOwner(ownerId: UUID, attachmentId: UUID): Boolean {
        return AttachmentDao.findById(attachmentId)?.ownerId == ownerId
    }
}