package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.api.course.element.model.*
import com.denchic45.studiversity.database.table.AttachmentDao
import com.denchic45.studiversity.database.table.AttachmentReferenceDao
import com.denchic45.studiversity.database.table.AttachmentReferences
import com.denchic45.studiversity.database.table.Attachments
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll
import java.util.*


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

    fun addAttachmentReference(attachmentId: UUID, resourceId: UUID): AttachmentHeader {
        val attachmentDao =
            AttachmentDao.findById(attachmentId) ?: throw NotFoundException(AttachmentErrors.ATTACHMENT_NOT_FOUND)
        addAttachmentReference(attachmentDao, resourceId)
        return attachmentDao.toAttachmentResponse()
    }

    private fun addAttachmentReference(dao: AttachmentDao, resourceId: UUID) {
        AttachmentReferenceDao.new {
            this.attachment = dao
            this.resourceId = resourceId
        }
    }

    fun removeReference(attachmentId: UUID, resourceId: UUID) {
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