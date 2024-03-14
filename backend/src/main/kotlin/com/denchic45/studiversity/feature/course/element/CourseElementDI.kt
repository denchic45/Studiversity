package com.denchic45.studiversity.feature.course.element

import com.denchic45.studiversity.feature.course.element.usecase.FindCourseElementUseCase
import com.denchic45.studiversity.feature.course.element.usecase.FindCourseElementsByCourseIdUseCase
import com.denchic45.studiversity.feature.course.element.usecase.RemoveCourseElementUseCase
import com.denchic45.studiversity.feature.course.element.usecase.UpdateCourseElementUseCase
import com.denchic45.studiversity.feature.course.material.CourseMaterialRepository
import com.denchic45.studiversity.feature.course.material.usecase.AddCourseMaterialUseCase
import com.denchic45.studiversity.feature.course.material.usecase.FindCourseMaterialUseCase
import com.denchic45.studiversity.feature.course.material.usecase.UpdateCourseMaterialUseCase
import com.denchic45.studiversity.feature.course.work.CourseWorkRepository
import com.denchic45.studiversity.feature.course.work.usecase.*
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddCourseWorkUseCase(get(), get(), get(), get()) }
    single { FindCourseElementUseCase(get(), get()) }
    single { FindCourseWorkUseCase(get(), get()) }
    single { RemoveCourseElementUseCase(get(), get(), get()) }
    single { FindCourseElementsByCourseIdUseCase(get(), get()) }
    single { UpdateCourseElementUseCase(get(), get()) }
    single { UpdateCourseWorkUseCase(get(), get()) }
    single { FindCourseWorksUseCase(get(), get()) }

    single { AddCourseMaterialUseCase(get(), get()) }
    single { UpdateCourseMaterialUseCase(get(),get()) }
    single { FindCourseMaterialUseCase(get(),get()) }

    single { RequireExistsCourseElementUseCase(get(),get()) }
}

private val repositoryModule = module {
    single { CourseElementRepository() }
    single { CourseWorkRepository() }
    single { CourseMaterialRepository() }
}

val courseElementModule = module {
    includes(useCaseModule, repositoryModule)
}