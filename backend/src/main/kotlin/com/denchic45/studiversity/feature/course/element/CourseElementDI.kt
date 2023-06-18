package com.denchic45.studiversity.feature.course.element

import com.denchic45.studiversity.feature.course.element.usecase.FindCourseElementUseCase
import com.denchic45.studiversity.feature.course.element.usecase.FindCourseElementsByCourseIdUseCase
import com.denchic45.studiversity.feature.course.element.usecase.RemoveCourseElementUseCase
import com.denchic45.studiversity.feature.course.element.usecase.UpdateCourseElementUseCase
import com.denchic45.studiversity.feature.course.work.CourseWorkRepository
import com.denchic45.studiversity.feature.course.work.usecase.AddCourseWorkUseCase
import com.denchic45.studiversity.feature.course.work.usecase.FindCourseWorkUseCase
import com.denchic45.studiversity.feature.course.work.usecase.FindCourseWorksUseCase
import com.denchic45.studiversity.feature.course.work.usecase.UpdateCourseWorkUseCase
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
    single { FindCourseWorksUseCase(get(), get()) }
//    single { RemoveAttachmentOfCourseElementUseCase(get(), get()) }
}

private val repositoryModule = module {
    single { CourseElementRepository() }
    single { CourseWorkRepository() }
}

val courseElementModule = module {
    includes(useCaseModule, repositoryModule)
}