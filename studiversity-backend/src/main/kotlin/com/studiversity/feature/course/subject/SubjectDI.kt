package com.studiversity.feature.course.subject

import com.studiversity.feature.course.subject.usecase.*
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddSubjectUseCase(get()) }
    single { FindSubjectByIdUseCase(get()) }
    single { FindAllSubjectsUseCase(get()) }
    single { UpdateSubjectUseCase(get()) }
    single { RemoveSubjectUseCase(get()) }
}

private val repositoryModule = module {
    single { SubjectRepository() }
}

val subjectModule = module {
    includes(useCaseModule, repositoryModule)
}