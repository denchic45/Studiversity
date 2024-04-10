package com.denchic45.studiversity.ui.yourworks

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.push
import com.denchic45.studiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.loadingResource
import com.denchic45.studiversity.domain.usecase.FindYourSubmittedCourseWorksUseCase
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class YourSubmittedWorksComponent(
    private val findYourSubmittedCourseWorksUseCase: FindYourSubmittedCourseWorksUseCase,
    @Assisted
    private val rootNavigator: RootNavigator,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    val componentScope = componentScope()

    val refreshing = MutableStateFlow(true)

    fun onRefresh() {
        refreshing.value = true
    }

    val works: MutableStateFlow<Resource<List<CourseWorkResponse>>> =
        MutableStateFlow(loadingResource())

    init {
        componentScope.launch {
            works.emitAll(findYourSubmittedCourseWorksUseCase())
        }

        refreshing.filter { it }.onEach { forceUpdate() }
            .launchIn(componentScope)

        works.drop(1).onEach { refreshing.value = false }
            .launchIn(componentScope)
    }

    fun onWorkClick(courseId: UUID, workId: UUID) {
        rootNavigator.push(RootConfig.CourseWork(courseId, workId))
    }

    private suspend fun forceUpdate() {
        works.value = findYourSubmittedCourseWorksUseCase().last()
    }
}