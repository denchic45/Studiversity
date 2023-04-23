package com.studiversity.feature.course.element

import com.studiversity.feature.attachment.usecase.AddAttachmentUseCase
import com.studiversity.feature.course.element.repository.CourseElementRepository
import com.studiversity.feature.course.element.usecase.*
import com.studiversity.feature.course.work.usecase.AddCourseWorkUseCase
import com.studiversity.feature.course.work.usecase.FindCourseWorkUseCase
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddCourseWorkUseCase(get(), get(), get(), get()) }
    single { FindCourseElementUseCase(get(), get()) }
    single { FindCourseWorkUseCase(get(), get()) }
    single { RemoveCourseElementUseCase(get(), get(), get()) }
//    single { AddLinkAttachmentOfCourseElementUseCase(get(), get()) }
//    single { FindAttachmentsOfCourseElementUseCase(get(), get()) }
//    single { FindAttachmentOfCourseElementUseCase(get(), get()) }
    single { FindCourseElementsByCourseIdUseCase(get(), get()) }
    single { UpdateCourseElementUseCase(get(), get()) }
//    single { RemoveAttachmentOfCourseElementUseCase(get(), get()) }
}

private val repositoryModule = module {
    single { CourseElementRepository() }
}

val courseElementModule = module {
    includes(useCaseModule, repositoryModule)
}