package com.denchic45.studiversity.ui.yourworks

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.FindYourOverdueWorksUseCase
import com.denchic45.studiversity.domain.usecase.FindYourSubmittedCourseWorksUseCase
import com.denchic45.studiversity.domain.usecase.FindYourUpcomingWorksUseCase
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class YourSubmittedWorksComponent(
    findYourSubmittedCourseWorksUseCase: FindYourSubmittedCourseWorksUseCase,
    @Assisted
    private val onWorkOpen: (courseId:UUID, workId: UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    val componentScope = componentScope()

    val refreshing = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val works = refreshing.onStart { emit(true) }.filter { it }.flatMapLatest {
        findYourSubmittedCourseWorksUseCase()
    }.onEach { refreshing.update { false } }.stateInResource(componentScope)

    fun onWorkClick(courseId:UUID, workId: UUID) {
        onWorkOpen(courseId,workId)
    }
}