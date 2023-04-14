package com.denchic45.kts.ui.main

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.appVersion.GoogleAppVersionService
import com.denchic45.kts.MobileNavigationDirections
import com.denchic45.kts.R
import com.denchic45.kts.domain.*
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.FindYourCoursesUseCase
import com.denchic45.kts.ui.NavigationCommand
import com.denchic45.kts.ui.UiText
import com.denchic45.kts.ui.adapter.*
import com.denchic45.kts.ui.adminPanel.AdminPanelFragmentDirections
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.kts.ui.course.CourseFragmentDirections
import com.denchic45.kts.ui.onString
import com.denchic45.kts.ui.onVector
import com.denchic45.kts.ui.settings.SettingsFragmentDirections
import com.denchic45.kts.ui.tasks.TasksFragmentDirections
import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val appVersionService: GoogleAppVersionService,
    private val findYourCoursesUseCase: FindYourCoursesUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase
) : BaseViewModel() {

    private val screenIdsWithFab: Set<Int> = setOf(
        R.id.courseFragment,
        R.id.studyGroupEditorFragment
    )

    private val checkCapabilities = flow {
        emit(checkUserCapabilitiesInScopeUseCase(capabilities = emptyList()))
    }.stateInResource(viewModelScope)

    val updateBannerState = MutableStateFlow<UpdateBannerState>(UpdateBannerState.Hidden)

    fun setActivityForService(activity: Activity) {
        appVersionService.activityRef = WeakReference(activity)
    }

    private val mainScreenIds: Set<Int> = setOf(R.id.menu_timetable, R.id.menu_group)
    private val onNavItemClickActions = mapOf(
        R.string.nav_tasks to {
            navigateTo(TasksFragmentDirections.actionGlobalTasksFragment())
        },
        R.string.nav_duty_roster to { },
        R.string.nav_schedule to { },

        R.string.nav_control_panel to {
            navigateTo(AdminPanelFragmentDirections.actionGlobalMenuAdminPanel())
        },
        R.string.nav_settings to { navigateTo(SettingsFragmentDirections.actionGlobalMenuSettings()) },
        R.string.nav_help to { },
    )

    val fabVisibility: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)

    private var courseIds = emptyMap<String, String>()

    val goBack = MutableSharedFlow<Unit>()

    val closeNavMenu = MutableSharedFlow<Unit>()

    val menuBtnVisibility = MutableSharedFlow<Pair<Int, Boolean>>()

    val toolbarNavigationState = MutableStateFlow(ToolbarNavigationState.MENU)

    enum class ToolbarNavigationState { NONE, MENU, BACK }

    val userInfo = interactor.observeThisUser().filterNotNull()
        .stateInResource(viewModelScope)

//    private val uiPermissions: UiPermissions

    var openLogin = MutableSharedFlow<Unit>()

    val bottomMenuVisibility: MutableLiveData<Boolean> = MutableLiveData(true)

    val navMenuState: MutableStateFlow<Resource<NavMenu>> = MutableStateFlow(Resource.Loading)

    fun onOptionItemSelect(itemId: Int) {
        when (itemId) {
            android.R.id.home -> viewModelScope.launch { goBack.emit(Unit) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        interactor.removeListeners()
    }

    fun onResume() {
        appVersionService.observeDownloadedUpdate()
    }

    fun onProfileClick() {
        userInfo.value.onSuccess {
            navigateTo(MobileNavigationDirections.actionGlobalProfileFragment(it.id.toString()))
        }
    }

    fun onNavItemClick(position: Int) {
        navMenuState.value.onSuccess {
            val name = (it.items[position] as NavTextItem).name
            name.onVector {
                onNavItemClickActions.getValue(it).invoke()
            }.onString {
                viewModelScope.launch {
                    navigate.emit(
                        NavigationCommand.To(
                            CourseFragmentDirections.actionGlobalCourseFragment(

                                courseIds[it]!!
                            )
                        )
                    )

                }
            }
        }
    }

    fun onExpandCoursesClick() {
//        val item = navMenuState.value
        navMenuState.updateResource {
            it.copy(expandAllCourse = !it.expandAllCourse)
        }
//        navMenuState.value = item.copy(expandAllCourse = !item.expandAllCourse)
    }

    fun onDestinationChanged(id: Int) {
        if (mainScreenIds.contains(id)) {
            toolbarNavigationState.value = ToolbarNavigationState.MENU
            if (!bottomMenuVisibility.value!!) {
                bottomMenuVisibility.value = true
            }
        } else if (bottomMenuVisibility.value!!) {
            toolbarNavigationState.value = ToolbarNavigationState.BACK
            bottomMenuVisibility.value = false
        }

        if (!screenIdsWithFab.contains(id))
            fabVisibility.tryEmit(false)
    }

    companion object {
        private const val ALLOW_CONTROL = "ALLOW_CONTROL"
    }

    init {
        addCloseable(appVersionService)
        appVersionService.onUpdateDownloaded = {
            Log.d("lol", "startUpdate: toast DOWNLOADED")
            updateBannerState.value = UpdateBannerState.Install
        }

        appVersionService.onUpdateLoading = { progress, megabyteTotal ->
            updateBannerState.value =
                UpdateBannerState.Loading(progress, "$progress% из $megabyteTotal МБ")
        }

        appVersionService.observeUpdates(
            onUpdateAvailable = {
                updateBannerState.value = UpdateBannerState.Remind
            },
            onError = {
//                showToast("Ошибка")
//                it.printStackTrace()
//                showSnackBar(it.message ?: "Err...")
            }
        )

        viewModelScope.launch {
            navMenuState.emitAll(
                flow { emit(findYourCoursesUseCase()) }
                    .mapResource { courses ->
//                        checkCapabilities.mapResource { capabilities ->
                            NavMenu(
                                courses = courses,
                                hasGroup = true, // TODO: get actual value
                                isModerator = true // TODO: get actual value
                            )
//                        }
                    }
            )
        }

        viewModelScope.launch(Dispatchers.IO) { interactor.startListeners() }

        viewModelScope.launch {
            // TODO: Решить, что делать с этим
//            interactor.observeHasGroup().collect { hasGroup: Boolean ->
//                menuBtnVisibility.value = Pair(R.id.menu_group, hasGroup)
//            }
        }
        // TODO: Решить, что делать с этим
        viewModelScope.launch {
//            combine(
//                flow { emit(findYourCoursesUseCase()) },
//                checkCapabilities
//            ) { courses, capabilities ->
//                courses.map {
//                    capabilities.map {
//
//                    }
//                }
//                courseIds = courses.associate { it.name to it.id }
//                NavMenuState.NavMenu(
//                    courses,
//                    interactor.findThisUser(),
//                    capabilities
//                )
//            }.stateIn(
//                viewModelScope,
//                SharingStarted.WhileSubscribed(5000),
//                NavMenuState.NavMenuEmpty
//            ).collect {
//                navMenuItems.value = it
//            }
//        }

            viewModelScope.launch {
                interactor.listenAuthState
                    .collect { logged: Boolean ->
                        if (!logged) {
                            viewModelScope.launch { openLogin.emit(Unit) }
                        }
                    }
            }

//        uiPermissions = UiPermissions(interactor.findThisUser())
//        uiPermissions.putPermissions(Permission(ALLOW_CONTROL, { hasAdminPerms() }))
        }
    }

        fun onDownloadUpdateClick() {
            appVersionService.startDownloadUpdate()
            updateBannerState.value = UpdateBannerState.WaitLoading
        }

        fun onLaterUpdateClick() {
            updateBannerState.value = UpdateBannerState.Hidden
        }

        fun onInstallClick() {
            appVersionService.installUpdate()
        }

    data class NavMenu(
        private val courses: List<CourseResponse>,
        private val hasGroup: Boolean,
        private val isModerator: Boolean,
        val expandAllCourse: Boolean = false,
    ) {
        private val mainTextItems: MutableList<NavTextItem> = mutableListOf()
        private val footerTextItems: MutableList<NavTextItem> = mutableListOf()

        // TODO: Решить, что делать с этим
        init {
            with(mainTextItems) {
//                    if (user.isStudent)
//                        mainTextItems.add(
//                            NavTextItem(
//                                UiText.IdText(R.string.nav_tasks),
//                                UiText.IdText(R.drawable.ic_tasks)
//                            )
//                        )
                if (hasGroup) {
                    mainTextItems.add(
                        NavTextItem(
                            UiText.IdText(R.string.nav_duty_roster),
                            UiText.IdText(R.drawable.ic_clean),
                            enable = false
                        )
                    )
                }
                add(
                    NavTextItem(
                        UiText.IdText(R.string.nav_schedule),
                        UiText.IdText(R.drawable.ic_time),
                        enable = false
                    )
                )
            }
            with(footerTextItems) {
                if (isModerator) {
                    add(
                        NavTextItem(
                            UiText.IdText(R.string.nav_control_panel),
                            UiText.IdText(R.drawable.ic_control_panel)
                        )
                    )
                }
                addAll(
                    listOf(
                        NavTextItem(
                            UiText.IdText(R.string.nav_settings),
                            UiText.IdText(R.drawable.ic_settings)
                        ), NavTextItem(
                            UiText.IdText(R.string.nav_help),
                            UiText.IdText(R.drawable.ic_help),
                            enable = false
                        )
                    )
                )
            }
        }

        val items: List<NavItem> = create()

        private fun create(): List<NavItem> {
            return buildList {
                addAll(mainTextItems)
                add(DividerItem())
                if (courses.isNotEmpty()) {
                    val nameOfDropdownCoursesNavItem: Int
                    add(NavSubHeaderItem(UiText.IdText(R.string.nav_courses_my)))
                    val visibleCourses = if (expandAllCourse) {
                        nameOfDropdownCoursesNavItem = R.string.nav_courses_hide
                        courses
                    } else {
                        nameOfDropdownCoursesNavItem = R.string.nav_courses_show_all
                        courses.take(5)
                    }
                    addAll(visibleCourses.map {
                        NavTextItem(
                            UiText.StringText(it.name),
                            id = it.id,
                            iconType = NavTextItem.IconType.CIRCLE,
                            color = UiText.StringText("dark_blue")
                        )
                    })
                    if (courses.size > 5)
                        add(
                            NavDropdownItem(
                                UiText.IdText(nameOfDropdownCoursesNavItem),
                                expandAllCourse
                            )
                        )
                    add(
                        NavTextItem(
                            UiText.IdText(R.string.nav_courses_archive),
                            UiText.IdText(R.drawable.ic_archive),
                            enable = false
                        )
                    )
                    add(DividerItem())
                }
                addAll(footerTextItems)

            }
        }
    }

sealed class UpdateBannerState {

    object Hidden : UpdateBannerState()

    object Remind : UpdateBannerState()

    object WaitLoading : UpdateBannerState()

    data class Loading(
        val progress: Long,
        val info: String,
    ) : UpdateBannerState()

    object Install : UpdateBannerState()
}}