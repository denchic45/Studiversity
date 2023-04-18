package com.denchic45.kts.ui.courseWork

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.kts.ui.courseWork.details.CourseWorkDetailsComponent
import com.denchic45.kts.ui.courseWork.submissions.CourseWorkSubmissionsComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkComponent(
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    private val _courseWorkDetailsComponent: (
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkDetailsComponent,
    private val _courseWorkSubmissionsComponent: (
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkSubmissionsComponent,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val elementId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val children = flow {
        emit(
            checkUserCapabilitiesInScopeUseCase(
                scopeId = courseId,
                capabilities = listOf(Capability.ReadSubmissions, Capability.SubmitSubmission)
            )
        )
    }.mapResource {
        buildList {
          withContext(Dispatchers.Main) {
              add(
                  Child.Details(
                      _courseWorkDetailsComponent(
                          courseId,
                          elementId,
                          childContext("details")
                      )
                  )
              )
              it.ifHasCapability(Capability.ReadSubmissions) {
                  add(
                      Child.Submissions(
                          _courseWorkSubmissionsComponent(
                              courseId,
                              elementId,
                              childContext("submissions")
                          )
                      )
                  )
              }
          }
        }
    }.stateInResource(componentScope)

    sealed class Child(val title:String) {
        class Details(val component: CourseWorkDetailsComponent) : Child("Задание")
        class Submissions(val component: CourseWorkSubmissionsComponent) : Child("Ответы")
    }
}