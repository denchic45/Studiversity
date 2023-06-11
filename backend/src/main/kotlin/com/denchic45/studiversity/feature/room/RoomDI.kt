package com.denchic45.studiversity.feature.room

import com.denchic45.studiversity.feature.room.usecase.AddRoomUseCase
import com.denchic45.studiversity.feature.room.usecase.FindRoomByIdUseCase
import com.denchic45.studiversity.feature.room.usecase.RemoveRoomUseCase
import com.denchic45.studiversity.feature.room.usecase.UpdateRoomUseCase
import org.koin.dsl.module

private val useCaseModule = module {
    single { AddRoomUseCase(get(), get()) }
    single { FindRoomByIdUseCase(get(), get()) }
    single { UpdateRoomUseCase(get(), get()) }
    single { RemoveRoomUseCase(get(), get()) }
}

val roomModule = module {
    includes(useCaseModule)
    single { RoomRepository() }
}