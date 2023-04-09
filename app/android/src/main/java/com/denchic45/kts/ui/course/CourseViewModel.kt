package com.denchic45.kts.ui.course

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.data.domain.*
import com.denchic45.kts.domain.filterSuccess
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.*
import com.denchic45.kts.ui.AndroidUiComponent
import com.denchic45.kts.ui.AndroidUiComponentDelegate
import com.denchic45.kts.ui.courseelements.CourseElementsUiLogic
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class CourseViewModel @Inject constructor(
    @Named(CourseFragment.COURSE_ID) private val _courseId: String,
    findCourseByIdUseCase: FindCourseByIdUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    private val findCourseElementsUseCase: FindCourseElementsUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    findSelfUserUseCase: FindSelfUserUseCase,
    componentContext: ComponentContext
) : CourseElementsUiLogic(
    findCourseByIdUseCase,
    removeCourseElementUseCase,
    findCourseElementsUseCase,
    findSelfUserUseCase,
    _courseId.toUUID(),
    componentContext
), AndroidUiComponent by AndroidUiComponentDelegate(componentContext) {

    val courseId = _courseId.toUUID()

    val openTaskEditor = SingleLiveData<Triple<String?, String, String>>()
    val openTask = SingleLiveData<Pair<String, String>>()

    val openCourseEditor = SingleLiveData<String>()
    val openCourseSectionEditor = SingleLiveData<String>()

    private val capabilities = flow {
        emit(
            checkUserCapabilitiesInScopeUseCase(
                scopeId = courseId,
                capabilities = listOf(Capability.WriteCourseElements, Capability.WriteCourse)
            )
        )
    }.onEach {
        it.onSuccess { response ->
            val allowCourseEdit = response.hasCapability(Capability.WriteCourse)
            setMenuItemVisible(
                R.id.option_edit_course to allowCourseEdit,
                R.id.option_edit_sections to allowCourseEdit
            )
        }
    }.stateInResource(componentScope)

    val fabVisibility = capabilities.filterSuccess()
        .map { it.value.hasCapability(Capability.WriteCourseElements) }

    val course = flow { emit(findCourseByIdUseCase(_courseId.toUUID())) }
        .onEach {
            it.onFailure { when(it) {
                NoConnection -> TODO()
                NotFound -> finish()
                else -> finish()
            } }
        }.stateInResource(componentScope)

    private var oldPosition: Int = -1
    private var position: Int = -1

    override fun onCreateElement() {
        openTaskEditor.value = Triple(null, _courseId, "")
    }

    override fun onOpenElement(elementId: UUID) {
        openTask.value = elementId.toString() to _courseId
    }

    fun onFabClick() {
        onCreateElement()
    }

    override fun onOptionClick(itemId: Int) {
        when (itemId) {
            R.id.option_edit_course -> openCourseEditor.value = _courseId
            R.id.option_edit_sections -> openCourseSectionEditor.value = _courseId
        }
    }

    fun onContentMove(oldPosition: Int, position: Int) {
//        if (this.oldPosition == -1)
//            this.oldPosition = oldPosition
//        this.position = position
//        Collections.swap(showContents.value!!, oldPosition, position)
//
//        showContents.value = showContents.value
    }

    fun onContentMoved() {
//        if (oldPosition == position) return
//
//        componentScope.launch {
//            val contents = showContents.value!!
//
//            val prevOrder: Int = if (position == 0 || contents[position - 1] !is CourseContent) 0
//            else (contents[position - 1] as CourseContent).order.toInt()
//
//            val nextOrder: Int =
//                if (position == contents.size - 1) ((contents[position - 1] as CourseContent).order + (1024 * 2)).toInt()
//                else (contents[position + 1] as CourseContent).order.toInt()
//
//            updateCourseContentOrderUseCase(
//                contents[position].id,
//                Orders.getBetweenOrders(prevOrder, nextOrder)
//            )
//            oldPosition = -1
//            position = -1
//        }
    }

    companion object {
        const val ALLOW_COURSE_EDIT = "PERMISSION_EDIT"
        const val ALLOW_ADD_COURSE_CONTENT = "ALLOW_ADD_COURSE_CONTENT"
    }
}
