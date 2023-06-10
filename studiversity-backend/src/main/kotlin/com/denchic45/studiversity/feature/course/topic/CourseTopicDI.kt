package com.denchic45.studiversity.feature.course.topic

import com.denchic45.studiversity.feature.course.topic.usecase.*
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddCourseTopicUseCase(get(), get()) }
    single { UpdateCourseTopicUseCase(get(), get()) }
    single { RemoveCourseTopicUseCase(get(), get()) }
    single { FindCourseTopicUseCase(get(), get()) }
    single { FindCourseTopicsByCourseUseCase(get(), get()) }
}

private val repositoryModule = module { single { CourseTopicRepository() } }

val courseTopicModule = module {
    includes(useCaseModule, repositoryModule)
}