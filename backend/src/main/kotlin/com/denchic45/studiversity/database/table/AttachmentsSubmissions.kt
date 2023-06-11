package com.denchic45.studiversity.database.table

//object AttachmentsSubmissions : Table("attachment_submission") {
//    val attachmentId = uuid("attachment_id").references(Attachments.id, onDelete = ReferenceOption.CASCADE)
//    val submissionId = uuid("submission_id").references(Submissions.id, onDelete = ReferenceOption.RESTRICT)
//
//    init {
//        uniqueIndex("attachment_submission_un", attachmentId, submissionId)
//    }
//}