package com.denchic45.studiversity.feature.specialty

import com.denchic45.studiversity.feature.specialty.usecase.*
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddSpecialtyUseCase(get(), get()) }
    single { FindSpecialtyByIdUseCase(get(), get()) }
    single { UpdateSpecialtyUseCase(get(), get()) }
    single { SearchSpecialtiesUseCase(get(),get()) }
    single { RemoveSpecialtyUseCase(get(), get()) }
}

val specialtyModule = module {
    includes(useCaseModule)
    single { SpecialtyRepository() }
}