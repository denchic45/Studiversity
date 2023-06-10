package com.denchic45.studiversity.feature.timetable

import com.denchic45.studiversity.feature.timetable.usecase.*
import org.koin.dsl.module

private val useCaseModule = module {
    single { PutTimetableUseCase(get(), get()) }
    single { FindTimetableUseCase(get(), get()) }
    single { PutTimetableOfDayUseCase(get(), get()) }
    single { FindTimetableOfDayUseCase(get(), get()) }
    single { RemoveTimetableUseCase(get(), get()) }
    single { RemoveTimetableOfDayUseCase(get(), get()) }
}

private val repositoryModule = module {
    single { TimetableRepository() }
}

val timetableModule = module {
    includes(useCaseModule, repositoryModule)
}