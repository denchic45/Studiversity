package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.usecase.RemoveSubjectUseCase
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.search.SubjectChooserComponent
import com.denchic45.studiversity.ui.subjecteditor.SubjectEditorComponent
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SubjectsAdminComponent(
    private val removeSubjectUseCase: RemoveSubjectUseCase,
    subjectChooserComponent: (onSelect: (SubjectResponse?) -> Unit, ComponentContext) -> SubjectChooserComponent,
    subjectEditorComponent: (onFinish: (Boolean) -> Unit, UUID?, ComponentContext) -> SubjectEditorComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    AdminSearchableComponent<SubjectResponse> {

    private val componentScope = componentScope()

    override val searchableComponent = subjectChooserComponent(
        { subject -> subject?.let(::onSelect) }, componentContext
    )

    private val slotNavigation = SlotNavigation<Config>()
    val childSlot = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.SubjectEditor -> Child.SubjectEditor(
                    subjectEditorComponent(
                        { edited ->
                            slotNavigation.dismiss()
                            if (edited) searchableComponent.forceUpdate()
                        },
                        config.subjectId,
                        context
                    )
                )
            }
        })

    override fun onSelect(item: SubjectResponse) {
        onEditClick(item.id)
    }

    override fun onAddClick() {
        slotNavigation.activate(Config.SubjectEditor(null))
    }

    override fun onEditClick(id: UUID) {
        slotNavigation.activate(Config.SubjectEditor(id))
    }

    override fun onRemoveClick(id: UUID) {
        componentScope.launch {
            removeSubjectUseCase(id)
                .onSuccess { searchableComponent.forceUpdate() }
        }
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class SubjectEditor(val subjectId: UUID?) : Config
    }

    sealed interface Child {
        class SubjectEditor(val component: SubjectEditorComponent) : Child
    }
}