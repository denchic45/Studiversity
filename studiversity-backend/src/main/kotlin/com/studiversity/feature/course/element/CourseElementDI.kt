package com.studiversity.feature.course.element

import com.studiversity.feature.course.element.repository.CourseElementRepository
import com.studiversity.feature.course.element.usecase.FindCourseElementUseCase
import com.studiversity.feature.course.element.usecase.FindCourseElementsByCourseIdUseCase
import com.studiversity.feature.course.element.usecase.RemoveCourseElementUseCase
import com.studiversity.feature.course.element.usecase.UpdateCourseElementUseCase
import com.studiversity.feature.course.work.usecase.AddCourseWorkUseCase
import com.studiversity.feature.course.work.usecase.FindCourseWorkUseCase
import com.studiversity.feature.course.work.usecase.UpdateCourseWorkUseCase
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
    single { UpdateCourseWorkUseCase(get(), get()) }
//    single { RemoveAttachmentOfCourseElementUseCase(get(), get()) }
}

private val repositoryModule = module {
    single { CourseElementRepository() }
}

val courseElementModule = module {
    includes(useCaseModule, repositoryModule)
}