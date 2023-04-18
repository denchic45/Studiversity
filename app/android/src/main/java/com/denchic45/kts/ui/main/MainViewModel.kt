package com.denchic45.kts.ui.main

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.appVersion.GoogleAppVersionService
import com.denchic45.kts.MobileNavigationDirections
import com.denchic45.kts.R
import com.denchic45.kts.domain.MainInteractor
import com.denchic45.kts.domain.filterSuccess
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.FindAssignedUserRolesInScopeUseCase
import com.denchic45.kts.domain.usecase.FindYourCoursesUseCase
import com.denchic45.kts.ui.NavigationCommand
import com.denchic45.kts.ui.UiIcon
import com.denchic45.kts.ui.UiText
import com.denchic45.kts.ui.adapter.DividerItem
import com.denchic45.kts.ui.adapter.NavDropdownItem
import com.denchic45.kts.ui.adapter.NavItem
import com.denchic45.kts.ui.adapter.NavSubHeaderItem
import com.denchic45.kts.ui.adapter.NavTextItem
import com.denchic45.kts.ui.adminPanel.AdminPanelFragmentDirections
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.kts.ui.course.CourseFragmentDirections
import com.denchic45.kts.ui.onResource
import com.denchic45.kts.ui.settings.SettingsFragmentDirections
import com.denchic45.kts.ui.tasks.TasksFragmentDirections
import com.denchic45.kts.ui.uiIconOf
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.UUID
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val appVersionService: GoogleAppVersionService,
    private val findYourCoursesUseCase: FindYourCoursesUseCase,
    private val findAssignedUserRolesInScopeUseCase: FindAssignedUserRolesInScopeUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
) : BaseViewModel() {

//    private val screenIdsWithFab: Set<Int> = setOf(
//        R.id.courseFragment,
//        R.id.studyGroupEditorFragment
//    )

    private val checkCapabilities = flow {
        emit(checkUserCapabilitiesInScopeUseCase(capabilities = emptyList()))
    }.stateInResource(viewModelScope)

    private val userRoles = flow {
        emit(findAssignedUserRolesInScopeUseCase())
    }.stateInResource(viewModelScope)

    val updateBannerState = MutableStateFlow<UpdateBannerState>(UpdateBannerState.Hidden)

    fun setActivityForService(activity: Activity) {
        appVersionService.activityRef = WeakReference(activity)
    }

    val mainScreenIds: Set<Int> = setOf(R.id.menu_timetable, R.id.menu_group)
//    private val onNavItemClickActions = mapOf(
//        R.string.nav_tasks to {
//            navigateTo(TasksFragmentDirections.actionGlobalTasksFragment())
//        },
//        R.string.nav_duty_roster to { },
//        R.string.nav_schedule to { },
//
//        R.string.nav_control_panel to {
//            navigateTo(AdminPanelFragmentDirections.actionGlobalMenuAdminPanel())
//        },
//        R.string.nav_settings to { navigateTo(SettingsFragmentDirections.actionGlobalMenuSettings()) },
//        R.string.nav_help to { },
//    )

//    val fabVisibility: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)

    val goBack = MutableSharedFlow<Unit>()

    val closeNavMenu = MutableSharedFlow<Unit>()

    val menuBtnVisibility = MutableSharedFlow<Pair<Int, Boolean>>()

    val toolbarNavigationState = MutableStateFlow(ToolbarNavigationState.MENU)

    enum class ToolbarNavigationState { NONE, MENU, BACK }

    val userInfo = interactor.observeThisUser().filterNotNull().stateInResource(viewModelScope)

//    private val uiPermissions: UiPermissions

    var openLogin = MutableSharedFlow<Unit>()

    val bottomMenuVisibility: MutableLiveData<Boolean> = MutableLiveData(true)

    private val yourCourses = flow { emit(findYourCoursesUseCase()) }

    val navMenuState: StateFlow<NavDrawerState> = yourCourses.filterSuccess()
        .combine(userRoles.filterSuccess()) { courses, roles ->
            NavDrawerState(courses.value, roles.value.roles.contains(Role.Moderator))
        }.stateIn(viewModelScope, SharingStarted.Lazily, NavDrawerState(emptyList(), false))

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

    fun onTopNavItemClick(name: UiText) {
        name.onResource {
            when (it) {
                R.string.nav_tasks -> {
                    navigateTo(TasksFragmentDirections.actionGlobalTasksFragment())
                }

                R.string.nav_duty_roster -> {}
                R.string.nav_schedule -> {}
            }
        }
    }

    fun onFooterNavItemClick(name: UiText) {
        name.onResource {
            when (it) {
                R.string.nav_control_panel -> {
                    navigateTo(AdminPanelFragmentDirections.actionGlobalMenuAdminPanel())
                }

                R.string.nav_settings -> {
                    navigateTo(SettingsFragmentDirections.actionGlobalMenuSettings())
                }

                R.string.nav_help -> {}
            }
        }
    }

    fun onCourseClick(courseId: UUID) {
        viewModelScope.launch {
            navigate.emit(
                NavigationCommand.To(
                    CourseFragmentDirections.actionGlobalCourseFragment(
                        courseId.toString()
                    )
                )
            )
        }
    }

//    fun onNavItemClick(position: Int) {
//        navMenuState.value.onSuccess { navMenu ->
//            val name = (navMenu.items[position] as NavTextItem).name
//            name.onResource {
//                onNavItemClickActions.getValue(it).invoke()
//            }.onString {
//                viewModelScope.launch {
//                    navigate.emit(
//                        NavigationCommand.To(
//                            CourseFragmentDirections.actionGlobalCourseFragment(
//                                navMenu.courses[position].id.toString()
//                            )
//                        )
//                    )
//
//                }
//            }
//        }
//    }

    fun onExpandCoursesClick() {
//        val item = navMenuState.value
//        navMenuState.updateResource {
//            it.copy(expandAllCourse = !it.expandAllCourse)
//        }
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

//        if (!screenIdsWithFab.contains(id))
//            fabInteractor.update { it.copy(visible = false) }
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

        appVersionService.observeUpdates(onUpdateAvailable = {
            updateBannerState.value = UpdateBannerState.Remind
        }, onError = {
//                showToast("Ошибка")
//                it.printStackTrace()
//                showSnackBar(it.message ?: "Err...")
        })

//        viewModelScope.launch {
//            navMenuState.emitAll(
//                flow { emit(findYourCoursesUseCase()) }
//                    .mapResource { courses ->
////                        checkCapabilities.mapResource { capabilities ->
//                        NavMenu(
//                            courses = courses,
//                            hasGroup = true, // TODO: get actual value
//                            isModerator = true // TODO: get actual value
//                        )
////                        }
//                    }
//            )
//        }

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
                interactor.listenAuthState.collect { logged: Boolean ->
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

    data class NavDrawerState(
        val courses: List<CourseResponse>,
        private val isModerator: Boolean,
    ) {
        val topItems = listOf(
            NavDrawerItem(
                UiText.IdText(R.string.nav_schedule), uiIconOf(R.drawable.ic_time), enabled = false
            ), NavDrawerItem(
                UiText.IdText(R.string.nav_tasks), uiIconOf(R.drawable.ic_tasks), enabled = false
            )
        )

        val footerItems = buildList {
            if (isModerator) {
                add(
                    NavDrawerItem(
                        UiText.IdText(R.string.nav_control_panel),
                        uiIconOf(R.drawable.ic_control_panel)
                    )
                )
            }
            add(
                NavDrawerItem(
                    UiText.IdText(R.string.nav_settings), uiIconOf(R.drawable.ic_settings)
                )
            )
            add(
                NavDrawerItem(
                    UiText.IdText(R.string.nav_help), uiIconOf(R.drawable.ic_help), enabled = false
                )
            )
        }
    }


    data class NavMenu(
        val courses: List<CourseResponse>,
        private val hasGroup: Boolean,
        private val isModerator: Boolean,
        val expandAllCourse: Boolean = false,
    ) {
        private val mainTextItems: MutableList<NavTextItem> = mutableListOf()
        private val footerTextItems: MutableList<NavTextItem> = mutableListOf()

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
                            enabled = false
                        )
                    )
                }
                add(
                    NavTextItem(
                        UiText.IdText(R.string.nav_schedule),
                        UiText.IdText(R.drawable.ic_time),
                        enabled = false
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
                            enabled = false
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
                    if (courses.size > 5) add(
                        NavDropdownItem(
                            UiText.IdText(nameOfDropdownCoursesNavItem), expandAllCourse
                        )
                    )
                    add(
                        NavTextItem(
                            UiText.IdText(R.string.nav_courses_archive),
                            UiText.IdText(R.drawable.ic_archive),
                            enabled = false
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
    }
}

data class NavDrawerItem(
    val name: UiText, val icon: UiIcon, var selected: Boolean = false, val enabled: Boolean = true,
)