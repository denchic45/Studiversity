package com.denchic45.kts.ui.main

import com.denchic45.kts.ui.UiIcon
import com.denchic45.kts.ui.UiText

//class MainComponent constructor(
//    private val interactor: MainInteractor,
////    private val appVersionService: GoogleAppVersionService,
//    private val findYourCoursesUseCase: FindYourCoursesUseCase,
//    private val yourTimetablesRootComponent: (ComponentContext) -> YourTimetablesRootComponent,
//    private val yourStudyGroupsRootComponent: (ComponentContext) -> YourStudyGroupsRootStackChildrenContainer,
//    private val findAssignedUserRolesInScopeUseCase: FindAssignedUserRolesInScopeUseCase,
//    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
//    private val componentContext: ComponentContext
//) : ComponentContext by componentContext {
//
//    private val componentScope = componentScope()
//
//    private val checkCapabilities = checkUserCapabilitiesInScopeUseCase(
//        capabilities = emptyList()
//    ).stateInResource(componentScope)
//
//    private val userRoles = flow {
//        emit(findAssignedUserRolesInScopeUseCase())
//    }.stateInResource(componentScope)
//
//    val updateBannerState = MutableStateFlow<UpdateBannerState>(UpdateBannerState.Hidden)
//
//    private val navigation = StackNavigation<RootConfig>()
//
//    val stack: Value<ChildStack<RootConfig, RootChild>> = childStack(
//        source = navigation,
//        initialConfiguration = RootConfig.YourTimetables,
//        childFactory = { config, componentContext ->
//            when (config) {
//                is RootConfig.YourTimetables -> RootChild.YourTimetables(
//                    yourTimetablesRootComponent(
//                        componentContext
//                    )
//                )
//
//                is RootConfig.YourStudyGroups -> RootChild.YourStudyGroups(
//                    yourStudyGroupsRootComponent(
//                        componentContext
//                    )
//                )
//            }
//        })
//
//    fun setActivityForService(activity: Activity) {
////        appVersionService.activityRef = WeakReference(activity)
//    }
//
//    val mainScreenIds: Set<Int> = setOf(R.id.menu_timetable, R.id.menu_group)
//
//    val goBack = MutableSharedFlow<Unit>()
//
//    val closeNavMenu = MutableSharedFlow<Unit>()
//
//    val menuBtnVisibility = MutableSharedFlow<Pair<Int, Boolean>>()
//
////    val toolbarNavigationState = MutableSharedFlow<ToolbarNavigationState>()
//
//    enum class ToolbarNavigationState { NONE, MENU, BACK }
//
//    val userInfo = interactor.observeThisUser().filterNotNull().stateInResource(componentScope)
//
////    private val uiPermissions: UiPermissions
//
//    var openLogin = MutableSharedFlow<Unit>()
//
//    val bottomMenuVisibility: MutableLiveData<Boolean> = MutableLiveData(true)
//
//    private val yourCourses = flow { emit(findYourCoursesUseCase()) }
//        .shareIn(componentScope, SharingStarted.Lazily)
//
//    val navMenuState: StateFlow<NavDrawerState> = yourCourses.filterSuccess()
//        .combine(userRoles.filterSuccess()) { courses, roles ->
//            NavDrawerState(courses.value, roles.value.roles.contains(Role.Moderator))
//        }.stateIn(componentScope, SharingStarted.Lazily, NavDrawerState(emptyList(), false))
//
//    fun onOptionItemSelect(itemId: Int) {
//        when (itemId) {
//            android.R.id.home -> componentScope.launch { goBack.emit(Unit) }
//        }
//    }
//
//    fun onResume() {
////        appVersionService.observeDownloadedUpdate()
//    }
//
//    fun onProfileClick() {
//        userInfo.value.onSuccess {
//            navigateTo(MobileNavigationDirections.actionGlobalProfileFragment(it.id.toString()))
//        }
//    }
//
//    fun onTopNavItemClick(name: UiText) {
//        name.onResource {
//            when (it) {
//                R.string.nav_tasks -> {
//                    navigateTo(TasksFragmentDirections.actionGlobalTasksFragment())
//                }
//
//                R.string.nav_duty_roster -> {}
//                R.string.nav_schedule -> {}
//            }
//        }
//    }
//
//    fun onFooterNavItemClick(name: UiText) {
//        name.onResource {
//            when (it) {
//                R.string.nav_control_panel -> {
//                    navigateTo(AdminPanelFragmentDirections.actionGlobalMenuAdminPanel())
//                }
//
//                R.string.nav_settings -> {
//                    navigateTo(SettingsFragmentDirections.actionGlobalMenuSettings())
//                }
//
//                R.string.nav_help -> {}
//            }
//        }
//    }
//
//    fun onCourseClick(courseId: UUID) {
//        componentScope.launch {
//            navigate.emit(
//                NavigationCommand.To(
//                    CourseFragmentDirections.actionGlobalCourseFragment(
//                        courseId.toString()
//                    )
//                )
//            )
//        }
//    }
//
////    fun onNavItemClick(position: Int) {
////        navMenuState.value.onSuccess { navMenu ->
////            val name = (navMenu.items[position] as NavTextItem).name
////            name.onResource {
////                onNavItemClickActions.getValue(it).invoke()
////            }.onString {
////                viewModelScope.launch {
////                    navigate.emit(
////                        NavigationCommand.To(
////                            CourseFragmentDirections.actionGlobalCourseFragment(
////                                navMenu.courses[position].id.toString()
////                            )
////                        )
////                    )
////
////                }
////            }
////        }
////    }
//
//    fun onExpandCoursesClick() {
////        val item = navMenuState.value
////        navMenuState.updateResource {
////            it.copy(expandAllCourse = !it.expandAllCourse)
////        }
////        navMenuState.value = item.copy(expandAllCourse = !item.expandAllCourse)
//    }
//
//    fun onDestinationChanged(id: Int) {
//        componentScope.launch {
//            if (mainScreenIds.contains(id)) {
//                if (!bottomMenuVisibility.value!!) {
//                    bottomMenuVisibility.value = true
//                }
//            } else if (bottomMenuVisibility.value!!) {
//                bottomMenuVisibility.value = false
//            }
//        }
//    }
//
//    init {
////        addCloseable(appVersionService)
////        appVersionService.onUpdateDownloaded = {
////            Log.d("lol", "startUpdate: toast DOWNLOADED")
////            updateBannerState.value = UpdateBannerState.Install
////        }
//
////        appVersionService.onUpdateLoading = { progress, megabyteTotal ->
////            updateBannerState.value =
////                UpdateBannerState.Loading(progress, "$progress% из $megabyteTotal МБ")
////        }
//
////        appVersionService.observeUpdates(onUpdateAvailable = {
////            updateBannerState.value = UpdateBannerState.Remind
////        }, onError = {
////                showToast("Ошибка")
////                it.printStackTrace()
////                showSnackBar(it.message ?: "Err...")
////        })
//
////        viewModelScope.launch {
////            navMenuState.emitAll(
////                flow { emit(findYourCoursesUseCase()) }
////                    .mapResource { courses ->
//////                        checkCapabilities.mapResource { capabilities ->
////                        NavMenu(
////                            courses = courses,
////                            hasGroup = true, // TODO: get actual value
////                            isModerator = true // TODO: get actual value
////                        )
//////                        }
////                    }
////            )
////        }
//
//        componentScope.launch(Dispatchers.IO) { interactor.startListeners() }
//
//        componentScope.launch {
//            interactor.observeHasGroup().collect { hasGroup: Boolean ->
//                menuBtnVisibility.emit(R.id.menu_group to hasGroup)
//            }
//        }
//        componentScope.launch {
//            interactor.listenAuthState.collect { logged: Boolean ->
//                if (!logged) {
//                    componentScope.launch { openLogin.emit(Unit) }
//                }
//            }
//        }
//    }
//
//    fun onDownloadUpdateClick() {
////        appVersionService.startDownloadUpdate()
//        updateBannerState.value = UpdateBannerState.WaitLoading
//    }
//
//    fun onLaterUpdateClick() {
//        updateBannerState.value = UpdateBannerState.Hidden
//    }
//
//    fun onInstallClick() {
////        appVersionService.installUpdate()
//    }
//
//    data class NavDrawerState(
//        val courses: List<CourseResponse>,
//        private val isModerator: Boolean,
//    ) {
//        val topItems = listOf(
//            NavDrawerItem(
//                UiText.ResourceText(R.string.nav_schedule),
//                uiIconOf(R.drawable.ic_time),
//                enabled = false
//            ), NavDrawerItem(
//                UiText.ResourceText(R.string.nav_tasks),
//                uiIconOf(R.drawable.ic_tasks),
//                enabled = false
//            )
//        )
//
//        val footerItems = buildList {
//            if (isModerator) {
//                add(
//                    NavDrawerItem(
//                        UiText.ResourceText(R.string.nav_control_panel),
//                        uiIconOf(R.drawable.ic_control_panel)
//                    )
//                )
//            }
//            add(
//                NavDrawerItem(
//                    UiText.ResourceText(R.string.nav_settings), uiIconOf(R.drawable.ic_settings)
//                )
//            )
//            add(
//                NavDrawerItem(
//                    UiText.ResourceText(R.string.nav_help),
//                    uiIconOf(R.drawable.ic_help),
//                    enabled = false
//                )
//            )
//        }
//    }
//
//    sealed class UpdateBannerState {
//
//        object Hidden : UpdateBannerState()
//
//        object Remind : UpdateBannerState()
//
//        object WaitLoading : UpdateBannerState()
//
//        data class Loading(
//            val progress: Long,
//            val info: String,
//        ) : UpdateBannerState()
//
//        object Install : UpdateBannerState()
//    }
//
//    @Parcelize
//    sealed class RootConfig : Parcelable {
//        object YourTimetables : RootConfig()
//        object YourStudyGroups : RootConfig()
//    }
//
//    sealed class RootChild {
//        abstract val component: RootStackChildrenContainer
//
//        class YourTimetables(override val component: YourTimetablesRootComponent) : RootChild()
//
//        class YourStudyGroups(override val component: YourStudyGroupsRootStackChildrenContainer) : RootChild()
//    }
//}
//
data class NavDrawerItem(
    val name: UiText,
    val icon: UiIcon,
    val selected: Boolean = false,
    val enabled: Boolean = true
)