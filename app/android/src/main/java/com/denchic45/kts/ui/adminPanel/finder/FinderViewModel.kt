package com.denchic45.kts.ui.adminPanel.finder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.MobileNavigationDirections
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.data.model.domain.ListItem
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.model.Subject
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.domain.usecase.*
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.kts.ui.base.chooser.toSearchState
import com.denchic45.kts.ui.confirm.ConfirmInteractor
import com.denchic45.kts.ui.model.UiImage
import com.denchic45.kts.util.NetworkException
import com.github.michaelbull.result.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class FinderViewModel @Inject constructor(
    @Named("options_user") userOptions: List<ListItem>,
    @Named("options_group") groupOptions: List<ListItem>,
    @Named("options_subject") subjectOptions: List<ListItem>,
    private val interactor: FinderInteractor,
    private val removeStudyGroupUseCase: RemoveStudyGroupUseCase,
    private val confirmInteractor: ConfirmInteractor,

    findUserByContainsNameUseCase: FindUserByContainsNameUseCase,
    findGroupByContainsNameUseCase: FindGroupByContainsNameUseCase,
    findSubjectByContainsNameUseCase: FindSubjectByContainsNameUseCase,
    findSpecialtyByContainsNameUseCase: FindSpecialtyByContainsNameUseCase,
    findCourseByContainsNameUseCase: FindCourseByContainsNameUseCase
) : BaseViewModel() {

    val finderEntities = MutableLiveData<List<ListItem>>()

//    val currentSelectedEntity = MutableStateFlow(POSITION_FIND_USERS)

    private val openSubject = SingleLiveData<String>()

    val openSubjectEditor = SingleLiveData<String>()

    val openGroupEditor = SingleLiveData<String>()

    val openSpecialtyEditor = SingleLiveData<String>()

    val openCourse = SingleLiveData<String>()

    val showOptions = SingleLiveData<Pair<Int, List<ListItem>>>()
    private val queryByName = MutableSharedFlow<String>()
    private val onFinderItemClickActions: List<(String) -> Unit> = listOf(
        { navigateTo(MobileNavigationDirections.actionGlobalProfileFragment(it)) },
        { navigateTo(MobileNavigationDirections.actionGlobalMenuGroup(it)) },
        { openSubject.setValue(it) },
        { openSpecialtyEditor.setValue(it) },
        { navigateTo(MobileNavigationDirections.actionGlobalCourseFragment(it)) })
    private val queryTexts = mutableListOf<String?>(null, null, null, null, null)
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

    private fun queryToSearchFlow(index: Int): StateFlow<SearchState<out DomainModel>> =
        queryNames[index].flatMapLatest { text ->
            flow {
                emit(SearchState.Loading)
                findUseCases[index].invoke(text).map { result ->
                    emit(
                        result.toSearchState()
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, SearchState.Start)

    val currentSearchState: Flow<SearchState<out DomainModel>> =
        currentSearch.flatMapLatest { index -> searchStates[index] }


    private val onOptionItemClickActions: Map<String, () -> Unit>

    private val findUseCases = listOf(
        findUserByContainsNameUseCase,
        findGroupByContainsNameUseCase,
        findSubjectByContainsNameUseCase,
        findSpecialtyByContainsNameUseCase,
        findCourseByContainsNameUseCase
    )

    private val optionsList: List<List<ListItem>>
    private var selectedEntity: DomainModel? = null
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
//        val item = foundEntities[currentSelectedEntity.value][position]
//        onFinderItemClickActions[currentSelectedEntity.value](item.id)

        val clicked = (currentSearchState as SearchState.Found<out DomainModel>).items[position]
        onFinderItemClickActions[currentSearch.value](clicked.id)
    }

    fun onFinderItemLongClick(position: Int) {
//        selectedEntity = (foundItems.value as Resource.Success).data[position]
//        showOptions.value = Pair(position, optionsList[currentSelectedEntity.value])
    }

    companion object {
        const val POSITION_FIND_USERS = 0
    }

    init {
        finderEntities.value = listOf(
            ListItem(
                id = "ITEM_FIND_USER",
                title = "Пользователи",
                icon = UiImage.IdImage(R.drawable.ic_user)
            ),
            ListItem(
                id = "ITEM_FIND_GROUP",
                title = "Группы",
                icon = UiImage.IdImage(R.drawable.ic_group)
            ),
            ListItem(
                id = "ITEM_FIND_SUBJECT",
                title = "Предметы",
                icon = UiImage.IdImage(R.drawable.ic_subject)
            ),
            ListItem(
                id = "ITEM_FIND_SPECIALTY",
                title = "Специальности",
                icon = UiImage.IdImage(R.drawable.ic_specialty)
            ),
            ListItem(
                id = "ITEM_FIND_COURSE",
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
                navigateTo(MobileNavigationDirections.actionGlobalProfileFragment(selectedEntity!!.id))
            },
            "OPTION_EDIT_USER" to {
                val selectedUser = selectedEntity as User
                navigateTo(
                    MobileNavigationDirections.actionGlobalUserEditorFragment(
                        userId = selectedUser.id,
                        role = selectedUser.role.toString(),
                        groupId = selectedUser.groupId
                    )
                )
            },
            "OPTION_DELETE_USER" to {
                viewModelScope.launch {
                    val selectedUser = selectedEntity as User
                    openConfirmation(
                        Pair(
                            "Удаление пользователя",
                            "Удаленного пользователя нельзя будет восстановить"
                        )
                    )

                    if (confirmInteractor.receiveConfirm()) {
                        try {
                            interactor.removeUser(selectedUser)
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
                    MobileNavigationDirections.actionGlobalMenuGroup(
                        selectedEntity!!.id
                    )
                )
            },
            "OPTION_EDIT_GROUP" to {
                openGroupEditor.setValue(
                    selectedEntity!!.id
                )
            },
            "OPTION_DELETE_GROUP" to {
                viewModelScope.launch {
                    openConfirmation(Pair("Удалить группу", "Вы точно уверены???"))
                    if (confirmInteractor.receiveConfirm()) {
                        viewModelScope.launch {
                            try {
                                removeStudyGroupUseCase(selectedEntity!!.id)
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
                openSubjectEditor.setValue(
                    selectedEntity!!.id
                )
            },
            "OPTION_DELETE_SUBJECT" to {
                viewModelScope.launch {
                    openConfirmation(Pair("Удалить предмет", "Вы точно уверены???"))
                    if (confirmInteractor.receiveConfirm()) {
                        viewModelScope.launch {
                            try {
                                interactor.removeSubject(selectedEntity as Subject)
                            } catch (e: Exception) {
                                if (e is NetworkException) {
                                    showToast(R.string.error_check_network)
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

sealed class SearchState<T> {
    object Start : SearchState<Nothing>()
    object Loading : SearchState<Nothing>()
    class Found<T>(val items: List<T>) : SearchState<T>()
    object NotFound : SearchState<Nothing>()
    object NoConnection : SearchState<Nothing>()
}