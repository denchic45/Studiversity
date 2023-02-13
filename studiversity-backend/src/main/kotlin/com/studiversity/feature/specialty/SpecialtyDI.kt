package com.studiversity.feature.specialty

import com.studiversity.feature.specialty.usecase.AddSpecialtyUseCase
import com.studiversity.feature.specialty.usecase.FindSpecialtyByIdUseCase
import com.studiversity.feature.specialty.usecase.RemoveSpecialtyUseCase
import com.studiversity.feature.specialty.usecase.UpdateSpecialtyUseCase
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddSpecialtyUseCase(get(), get()) }
    single { FindSpecialtyByIdUseCase(get(), get()) }
    single { UpdateSpecialtyUseCase(get(), get()) }
    single { RemoveSpecialtyUseCase(get(), get()) }
}

val specialtyModule = module {
    includes(useCaseModule)
    single { SpecialtyRepository() }
}