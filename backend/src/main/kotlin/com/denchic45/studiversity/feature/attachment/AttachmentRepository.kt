package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.database.table.AttachmentDao
import com.denchic45.studiversity.database.table.AttachmentReferenceDao
import com.denchic45.studiversity.database.table.AttachmentReferences
import com.denchic45.studiversity.database.table.Attachments
import com.denchic45.stuiversity.api.course.element.model.*
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll
import java.util.*

// TODO: Использовать свое хранилище вместо supabase
class AttachmentRepository(private val storage: AttachmentFileStorage) {


    fun addFileAttachment(
        request: CreateFileRequest,
        resourceId: UUID
    ): FileAttachmentHeader = AttachmentDao.new {
        this.name = request.name
        this.type = AttachmentType.FILE
        this.resourceId = resourceId
    }.also { dao ->
        addAttachmentReference(dao, resourceId)
        storage.writeFile(dao.id.value.toString(), request)
    }.toFileAttachmentHeader()


    fun addLinkAttachment(
        link: CreateLinkRequest,
        resourceId: UUID
    ): LinkAttachmentHeader {
        return AttachmentDao.new {
            this.name = "Link name" // TODO ставить реальное название
            this.type = AttachmentType.LINK
            this.url = link.url
            this.resourceId = resourceId
        }.also { dao ->
            addAttachmentReference(dao, resourceId)
        }.toLinkAttachmentHeader()
    }

    // TODO: В будущем передавать также consumerType? чтобы проверить его наличие, напр:
    //  consumerType может быть SUBMISSION

    fun addAttachmentReference(attachmentId: UUID, resourceId: UUID): AttachmentHeader {
        val attachmentDao = AttachmentDao.findById(attachmentId) ?: throw NotFoundException("ATTACHMENT_NOT_FOUND")
        addAttachmentReference(attachmentDao, resourceId)
        return attachmentDao.toAttachmentResponse()
    }

    private fun addAttachmentReference(dao: AttachmentDao, resourceId: UUID) {
     AttachmentReferenceDao.new {
            this.attachment = dao
            this.resourceId = resourceId
        }
    }

//    suspend fun removeByCourseId(courseId: UUID) {
//        CourseElements.select { CourseElements.courseId eq courseId }
//            .forEach { removeByCourseElementId(it[CourseElements.id].value) }
//    }

//    suspend fun removeByCourseElementId(elementId: UUID) {
//        Submissions.select { Submissions.courseWorkId eq elementId }.forEach {
//            removeByConsumer(it[Submissions.id].value)
//        }
//        removeByConsumer(elementId)
//    }

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

    suspend fun removeReference(attachmentId: UUID, resourceId: UUID) {
        val attachmentDao = AttachmentDao[attachmentId]
        if (attachmentDao.resourceId == resourceId) {
            storage.delete(attachmentId)
        } else {
            AttachmentReferences.deleteWhere {
                AttachmentReferences.attachmentId eq attachmentId and (AttachmentReferences.resourceId eq resourceId)
            }
        }
    }

    fun removeByResourceId(resourceId: UUID) {
        val attachments = AttachmentDao.wrapRows(
            Attachments.innerJoin(AttachmentReferences, { Attachments.id }, { attachmentId })
                .selectAll()
                .where { AttachmentReferences.resourceId eq resourceId }
        )

        storage.deleteAll(attachments.mapNotNull { if (it.type == AttachmentType.FILE) it.id.value else null })
        attachments.forEach(AttachmentDao::delete)
    }

    fun findAttachmentsByResourceId(resourceId: UUID): List<AttachmentHeader> {
        return AttachmentDao.wrapRows(
            Attachments.innerJoin(AttachmentReferences, { Attachments.id }, { attachmentId })
                .selectAll()
                .where(Attachments.resourceId eq resourceId and (AttachmentReferences.resourceId eq resourceId))
        ).map { (it).toAttachmentResponse() }
    }

    fun isFileExists(attachmentId: UUID): Boolean = storage.exists(attachmentId)

    fun findFileSource(attachmentId: UUID): FileAttachmentResponse {
        val dao = AttachmentDao[attachmentId]
        return FileAttachmentResponse(attachmentId, storage.getSource(attachmentId), dao.name)
    }

    fun checkIsOwner(ownerId: UUID, attachmentId: UUID): Boolean {
        return AttachmentDao.findById(attachmentId)?.resourceId == ownerId
    }

    fun findResourceTypeByAttachmentId(attachmentId: UUID): String {
        return AttachmentDao[attachmentId].resourceType
    }
}