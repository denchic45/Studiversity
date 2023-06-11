package com.denchic45.studiversity.database.table

//object AttachmentsCourseElements : Table("attachment_course_element") {
//    val attachmentId = uuid("attachment_id").references(Attachments.id, onDelete = ReferenceOption.CASCADE)
//    val courseElementId = uuid("course_element_id").references(CourseElements.id, onDelete = ReferenceOption.RESTRICT)
//
//    init {
//        uniqueIndex("attachment_course_element_un", attachmentId, courseElementId)
//    }
//}