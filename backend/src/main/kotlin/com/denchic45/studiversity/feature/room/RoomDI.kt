package com.denchic45.studiversity.feature.room

import com.denchic45.studiversity.feature.room.usecase.*
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddRoomUseCase(get(), get()) }
    single { FindRoomByIdUseCase(get(), get()) }
    single { SearchRoomsUseCase(get(),get()) }
    single { UpdateRoomUseCase(get(), get()) }
    single { RemoveRoomUseCase(get(), get()) }
}

val roomModule = module {
    includes(useCaseModule)
    single { RoomRepository() }
}