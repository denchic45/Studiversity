package com.denchic45.kts.data.usecase

import kotlinx.coroutines.flow.Flow

abstract class FlowUseCase<out T, P> {

    abstract operator fun invoke(params: P): Flow<T>
}