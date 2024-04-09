package com.denchic45.studiversity.ui.courseelements

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.course.element.model.CourseElementResponse
import com.denchic45.studiversity.api.course.element.model.CourseElementType
import com.denchic45.studiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.studiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.FindCourseElementsUseCase
import com.denchic45.studiversity.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.studiversity.ui.coursematerial.CourseMaterialComponent
import com.denchic45.studiversity.ui.coursematerialeditor.CourseMaterialEditorComponent
import com.denchic45.studiversity.ui.coursework.CourseWorkComponent
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorComponent
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseElementsComponent(
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    private val findCourseElementsUseCase: FindCourseElementsUseCase,
    courseWorkComponent: (
        RootNavigator,
        onFinish: () -> Unit,
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkComponent,
    courseMaterialComponent: (
        onFinish: () -> Unit,
        courseId: UUID,
        materialId: UUID,
        ComponentContext,
    ) -> CourseMaterialComponent,
    courseWorkEditorComponent: (
        onFinish: () -> Unit,
        courseId: UUID,
        workId: UUID?,
        topicId: UUID?,
        ComponentContext,
    ) -> CourseWorkEditorComponent,
    courseMaterialEditorComponent: (
        onFinish: (CourseMaterialResponse) -> Unit,
        courseId: UUID,
        materialId: UUID?,
        topicId: UUID?,
        ComponentContext,
    ) -> CourseMaterialEditorComponent,
    @Assisted
    private val rootNavigator: RootNavigator,
    @Assisted
    private val courseId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val refreshing = MutableStateFlow(false)

    private val slotNavigation = SlotNavigation<SlotConfig>()
    private val childSlot = childSlot(
        source = slotNavigation,
        childFactory = { config, context ->
            when (config) {
//                is SlotConfig.CourseWork -> SlotChild.CourseWork(
//                    courseWorkComponent(
//                        rootNavigator,
//                        { slotNavigation.dismiss() },
//                        courseId,
//                        config.workId,
//                        context
//                    )
//                )

//                is SlotConfig.CourseMaterial -> SlotChild.CourseMaterial(
//                    courseMaterialComponent(
//                        rootNavigator,
//                        { slotNavigation.dismiss() },
//                        courseId,
//                        config.materialId,
//                        context
//                    )
//                )

                is SlotConfig.CourseWorkEditor -> SlotChild.CourseWorkEditor(
                    courseWorkEditorComponent(
                        { slotNavigation.dismiss() },
                        courseId,
                        config.workId,
                        config.topicId,
                        context
                    )
                )

                is SlotConfig.CourseMaterialEditor -> SlotChild.CourseMaterialEditor(
                    courseMaterialEditorComponent(
                        { slotNavigation.dismiss() },
                        courseId,
                        config.materialId,
                        config.topicId,
                        context
                    )
                )
            }
        }
    )

    private val _elements = flow { emit(findCourseElementsUseCase(courseId)) }
        .stateInResource(componentScope)

    val elements = MutableStateFlow<Resource<List<Pair<CourseTopicResponse?, List<CourseElementResponse>>>>>(
        resourceOf()
    )

    init {
        componentScope.launch {
            elements.emitAll(_elements)
        }
    }

    fun onItemClick(elementId: UUID, type: CourseElementType) {
        rootNavigator.push(
            when (type) {
                CourseElementType.WORK -> RootConfig.CourseWork(courseId, elementId)
                CourseElementType.MATERIAL -> RootConfig.CourseMaterial(courseId, elementId)
            }
        )
    }

    fun onAddWorkClick() {
        slotNavigation.activate(SlotConfig.CourseWorkEditor(null, null))
    }

    fun onAddMaterialClick() {
        slotNavigation.activate(SlotConfig.CourseMaterialEditor(null, null))
    }

    fun onContentMove(oldPosition: Int, position: Int) {
////        if (this.oldPosition == -1)
////            this.oldPosition = oldPosition
////        this.position = position
////        Collections.swap(showContents.value!!, oldPosition, position)
////
////        showContents.value = showContents.value
    }

    //
    fun onContentMoved() {
////        if (oldPosition == position) return
////
////        componentScope.launch {
////            val contents = showContents.value!!
////
////            val prevOrder: Int = if (position == 0 || contents[position - 1] !is CourseContent) 0
////            else (contents[position - 1] as CourseContent).order.toInt()
////
////            val nextOrder: Int =
////                if (position == contents.size - 1) ((contents[position - 1] as CourseContent).order + (1024 * 2)).toInt()
////                else (contents[position + 1] as CourseContent).order.toInt()
////
////            updateCourseContentOrderUseCase(
////                contents[position].id,
////                Orders.getBetweenOrders(prevOrder, nextOrder)
////            )
////            oldPosition = -1
////            position = -1
////        }
    }

    fun onRefresh() {
        componentScope.launch {
            refreshing.update { true }
            elements.update { findCourseElementsUseCase(courseId) }
            refreshing.update { false }
        }
    }

    @Parcelize
    sealed class SlotConfig : Parcelable {
//        data class CourseWork(val workId: UUID) : SlotConfig()

//        data class CourseMaterial(val materialId: UUID) : SlotConfig()

        data class CourseWorkEditor(val workId: UUID?, val topicId: UUID?) : SlotConfig()

        data class CourseMaterialEditor(val materialId: UUID?, val topicId: UUID?) : SlotConfig()
    }

    sealed class SlotChild {
        data class CourseWork(val component: CourseWorkComponent) : SlotChild()

        data class CourseMaterial(val component: CourseMaterialComponent) : SlotChild()

        class CourseWorkEditor(val component: CourseWorkEditorComponent) : SlotChild()

        class CourseMaterialEditor(val component: CourseMaterialEditorComponent) : SlotChild()
    }
}