package com.denchic45.studiversity.ui.yourworks

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.push
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.FindYourOverdueWorksUseCase
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class YourOverdueWorksComponent(
    findYourOverdueWorksUseCase: FindYourOverdueWorksUseCase,
    @Assisted
    private val rootNavigator: RootNavigator,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    val componentScope = componentScope()

    val refreshing = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val works = refreshing.onStart { emit(true) }.filter { it }.flatMapLatest {
        findYourOverdueWorksUseCase()
    }.onEach { refreshing.update { false } }.stateInResource(componentScope)

    fun onWorkClick(courseId: UUID, workId: UUID) {
        rootNavigator.push(RootConfig.CourseWork(courseId, workId))
    }
}