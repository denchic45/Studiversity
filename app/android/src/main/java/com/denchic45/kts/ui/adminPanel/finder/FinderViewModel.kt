package com.denchic45.kts.ui.adminPanel.finder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.MobileNavigationDirections
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.data.domain.*
import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.data.model.domain.ListItem
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.*
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.kts.ui.confirm.ConfirmInteractor
import com.denchic45.kts.ui.model.UiImage
import com.denchic45.kts.util.NetworkException
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.toUUID
import com.github.michaelbull.result.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class FinderViewModel @Inject constructor(
    @Named("options_user") userOptions: List<ListItem>,
    @Named("options_group") groupOptions: List<ListItem>,
    @Named("options_subject") subjectOptions: List<ListItem>,
    private val confirmInteractor: ConfirmInteractor,

    findUserByContainsNameUseCase: FindUserByContainsNameUseCase,
    findStudyGroupByContainsNameUseCase: FindStudyGroupByContainsNameUseCase,
    findSubjectByContainsNameUseCase: FindSubjectByContainsNameUseCase,
    findSpecialtyByContainsNameUseCase: FindSpecialtyByContainsNameUseCase,
    findCourseByContainsNameUseCase: FindCourseByContainsNameUseCase,
    removeUserUseCase: RemoveUserUseCase,
    removeStudyGroupUseCase: RemoveStudyGroupUseCase,
    removeSubjectUseCase: RemoveSubjectUseCase,
) : BaseViewModel() {

    val finderEntities = MutableLiveData<List<ListItem>>()

//    val currentSelectedEntity = MutableStateFlow(POSITION_FIND_USERS)

    private val openSubject = SingleLiveData<String>()

    val openSubjectEditor = SingleLiveData<String>()

    val openGroupEditor = SingleLiveData<String>()

    val openSpecialtyEditor = SingleLiveData<String>()

    val openCourse = SingleLiveData<String>()

    val showOptions = SingleLiveData<Pair<Int, List<ListItem>>>()
    private val onFinderItemClickActions: List<(String) -> Unit> = listOf(
        { navigateTo(MobileNavigationDirections.actionGlobalProfileFragment(it)) },
        { navigateTo(MobileNavigationDirections.actionGlobalStudyGroupFragment(it)) },
        { openSubject.setValue(it) },
        { openSpecialtyEditor.setValue(it) },
        { navigateTo(MobileNavigationDirections.actionGlobalCourseFragment(it)) })


    private val onGetItemId: List<(Any) -> String> = listOf(
        { (it as UserResponse).id.toString() },
        { (it as StudyGroupResponse).id.toString() },
        { (it as SubjectResponse).id.toString() },
        { (it as SpecialtyResponse).id.toString() },
        { (it as CourseResponse).id.toString() })

    private val startEmptyList: List<DomainModel> = emptyList()

    val foundItems = MutableStateFlow<Resource<List<DomainModel>>>(Resource.Success(startEmptyList))


    val currentSearch = MutableStateFlow(0)

    private val queryNames = listOf(
        MutableStateFlow(""),
        MutableStateFlow(""),
        MutableStateFlow(""),
        MutableStateFlow(""),
        MutableStateFlow("")
    )

    private val searchStates = listOf(
        queryToSearchFlow(0),
        queryToSearchFlow(1),
        queryToSearchFlow(2),
        queryToSearchFlow(3),
        queryToSearchFlow(4),
    )

    private fun queryToSearchFlow(index: Int): StateFlow<Resource<List<Any>>> {
        return queryNames[index].map { text ->
            findUseCases[index].invoke(text)
        }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentItems = currentSearch.flatMapLatest { index -> searchStates[index] }
        .stateInResource(viewModelScope)


    private val onOptionItemClickActions: Map<String, () -> Unit>

    private val findUseCases = listOf(
        findUserByContainsNameUseCase,
        findStudyGroupByContainsNameUseCase,
        findSubjectByContainsNameUseCase,
        findSpecialtyByContainsNameUseCase,
        findCourseByContainsNameUseCase
    )

    private val optionsList: List<List<ListItem>>


    private var selectedEntityId: String? = null

    fun onQueryTextSubmit(queryName: String) {

        queryNames[currentSearch.value].value = queryName

        viewModelScope.launch {
//            queryTexts[currentSelectedEntity.value] = queryName
//            queryByName.emit(queryName)
        }
    }

    fun onCurrentSearchSelect(position: Int) {
//        currentSelectedEntity.value = position
//        val items = foundEntities[currentSelectedEntity.value]
//        foundItems.tryEmit(Resource.Success(items))
//
//        queryTexts[position]?.let {
//            viewModelScope.launch {
//                queryByName.emit(it)
//            }
//        }


        currentSearch.value = position
    }

    fun onOptionClick(optionId: String) {
        onOptionItemClickActions[optionId]!!()
    }

    fun onFinderItemClick(position: Int) {
        currentItems.value.onSuccess {
            onFinderItemClickActions[currentSearch.value](
                onGetItemId[currentSearch.value](it[position])
            )
        }
    }

    fun onFinderItemLongClick(position: Int) {
        currentItems.value.onSuccess {
            selectedEntityId = onGetItemId[currentSearch.value](it[position])
            showOptions.value = Pair(position, optionsList[position])
        }
    }

    companion object {
        const val POSITION_FIND_USERS = 0
    }

    init {
        finderEntities.value = listOf(
            ListItem(
                title = "Пользователи",
                icon = UiImage.IdImage(R.drawable.ic_user)
            ),
            ListItem(
                title = "Группы",
                icon = UiImage.IdImage(R.drawable.ic_group)
            ),
            ListItem(
                title = "Предметы",
                icon = UiImage.IdImage(R.drawable.ic_subject)
            ),
            ListItem(
                title = "Специальности",
                icon = UiImage.IdImage(R.drawable.ic_specialty)
            ),
            ListItem(
                title = "Курсы",
                icon = UiImage.IdImage(R.drawable.ic_course)
            ),
        )
        optionsList = listOf(userOptions, groupOptions, subjectOptions)

//        viewModelScope.launch {
//            queryByName.flatMapLatest { name: String ->
//                findUseCases[currentSelectedEntity.value].invoke(name)
//            }.collect { resource ->
//                foundItems.value = resource
//                if (resource is Resource.Success)
//                    foundEntities[currentSelectedEntity.value] = resource.data
//            }
//        }

        onOptionItemClickActions = mapOf(
            "OPTION_SHOW_PROFILE" to {
                navigateTo(MobileNavigationDirections.actionGlobalProfileFragment(selectedEntityId!!))
            },
            "OPTION_EDIT_USER" to {
                navigateTo(
                    MobileNavigationDirections.actionGlobalUserEditorFragment(
                        userId = selectedEntityId!!
                    )
                )
            },
            "OPTION_DELETE_USER" to {
                viewModelScope.launch {
                    openConfirmation(
                        Pair(
                            "Удаление пользователя",
                            "Удаленного пользователя нельзя будет восстановить"
                        )
                    )

                    if (confirmInteractor.receiveConfirm()) {
                        try {
                            removeUserUseCase(selectedEntityId!!.toUUID())
                        } catch (e: Exception) {
                            if (e is NetworkException) {
                                showToast(R.string.error_check_network)
                            }
                        }
                    }
                }
            },
            "OPTION_SHOW_GROUP" to {
                navigateTo(
                    MobileNavigationDirections.actionGlobalStudyGroupFragment(
                        selectedEntityId!!
                    )
                )
            },
            "OPTION_EDIT_GROUP" to { openGroupEditor.setValue(selectedEntityId!!) },
            "OPTION_DELETE_GROUP" to {
                viewModelScope.launch {
                    openConfirmation(Pair("Удалить группу", "Вы точно уверены???"))
                    if (confirmInteractor.receiveConfirm()) {
                        viewModelScope.launch {
                            try {
                                removeStudyGroupUseCase(selectedEntityId!!.toUUID())
                            } catch (e: Exception) {
                                if (e is NetworkException) {
                                    showToast(R.string.error_check_network)
                                }
                            }
                        }
                    }
                }
            },
            "OPTION_SHOW_SUBJECT" to {},
            "OPTION_EDIT_SUBJECT" to {
                openSubjectEditor.setValue(selectedEntityId!!)
            },
            "OPTION_DELETE_SUBJECT" to {
                viewModelScope.launch {
                    openConfirmation(Pair("Удалить предмет", "Вы точно уверены???"))
                    if (confirmInteractor.receiveConfirm()) {
                        viewModelScope.launch {
                            removeSubjectUseCase(selectedEntityId!!.toUUID())
                                .onFailure {
                                    when (it) {
                                        NoConnection -> showToast(R.string.error_check_network)
                                        else -> showToast(R.string.error_check_network)
                                    }
                                }
                        }
                    }
                }
            }
        )
    }
}

//sealed class SearchState<T> {
//    object Start : SearchState<Nothing>()
//    object Loading : SearchState<Nothing>()
//    class Found<T>(val items: List<T>) : SearchState<T>()
//    object NotFound : SearchState<Nothing>()
//    object NoConnection : SearchState<Nothing>()
//}