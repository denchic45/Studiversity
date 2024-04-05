package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.studiversity.domain.usecase.RemoveSpecialtyUseCase
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.search.SpecialtyChooserComponent
import com.denchic45.studiversity.ui.specialtyeditor.SpecialtyEditorComponent
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class SpecialtiesAdminComponent(
    private val removeSpecialtyUseCase: RemoveSpecialtyUseCase,
    specialtyChooserComponent: (onSelect: (SpecialtyResponse?) -> Unit, ComponentContext) -> SpecialtyChooserComponent,
    specialtyEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> SpecialtyEditorComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SearchableAdminComponent<SpecialtyResponse> {

    private val componentScope = componentScope()

    override val chooserComponent = specialtyChooserComponent(
        { specialty -> specialty?.let(::onSelect) }, componentContext
    )

    private val slotNavigation = SlotNavigation<Config>()
    val childSlot: Value<ChildSlot<Config, Child>> = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.SpecialtyEditor -> Child.SpecialtyEditor(
                    specialtyEditorComponent(
                        slotNavigation::dismiss,
                        config.specialtyId,
                        context
                    )
                )
            }
        })

    override fun onSelect(item: SpecialtyResponse) {
        onEditClick(item.id)
    }

    override fun onAddClick() {
        slotNavigation.activate(Config.SpecialtyEditor(null))
    }

    override fun onEditClick(id: UUID) {
        slotNavigation.activate(Config.SpecialtyEditor(id))
    }

    override fun onRemoveClick(id: UUID) {
        componentScope.launch {
            removeSpecialtyUseCase(id)
        }
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class SpecialtyEditor(val specialtyId: UUID?) : Config
    }

    sealed interface Child {
        class SpecialtyEditor(val component: SpecialtyEditorComponent) : Child
    }
}