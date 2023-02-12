package com.studiversity.feature.attachment

import com.studiversity.feature.course.element.usecase.FindAttachmentOfCourseElementUseCase
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.getAttachmentOfCourseElement() {
    val findAttachmentOfCourseElement: FindAttachmentOfCourseElementUseCase by inject()

    get {

    }
}