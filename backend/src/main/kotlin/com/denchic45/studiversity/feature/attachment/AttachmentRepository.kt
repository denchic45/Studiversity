package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.database.table.*
import com.denchic45.stuiversity.api.course.element.model.*
import io.github.jan.supabase.storage.BucketApi
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import java.util.*

// TODO: Использовать свое хранилище вместо supabase
class AttachmentRepository(private val bucket: BucketApi) {

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

        // todo заменить на использование своего хранилища
//        bucket.deleteRecursive("courses/$courseId")
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

    suspend fun findAttachmentById(attachmentId: UUID): AttachmentResponse? {
        return AttachmentDao.findById(attachmentId)?.toAttachment()
    }

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