package com.denchic45.kts.ui.studygroup

//@Inject
//class GroupComponent(
//    studyGroupMembersComponent: (UUID, ComponentContext) -> StudyGroupMembersComponent,
//    groupCourseComponent: (groupId: UUID,ComponentContext) -> StudyGroupCoursesComponent,
//    @Assisted
//    private val studyGroupId: UUID,
//    @Assisted
//    componentContext: ComponentContext,
//) : ComponentContext by componentContext {
//
//
//    private val navigation = StackNavigation<GroupTabsConfig>()
//    val stack = childStack(source = navigation,
//        initialConfiguration = GroupTabsConfig.Members,
//        childFactory = { tabConfig: GroupTabsConfig, _ ->
//            when (tabConfig) {
//                is GroupTabsConfig.Members -> {
//                    StudyGroupTabsChild.Members(studyGroupMembersComponent(studyGroupId, componentContext))
//                }
//                is GroupTabsConfig.Courses -> {
//                    StudyGroupTabsChild.Courses(groupCourseComponent(studyGroupId,componentContext))
//                }
//            }
//        })
//
//    val tabs: StateFlow<List<TabItem>> = MutableStateFlow(listOf(TabItem.Members, TabItem.Courses))
//    val selectedTab = MutableStateFlow(0)
//
//    fun onTabClick(index: Int) {
//        selectedTab.value = index
//        navigation.bringToFront(
//            when (tabs.value[index]) {
//                is TabItem.Members -> GroupTabsConfig.Members
//                is TabItem.Courses -> GroupTabsConfig.Courses
//                TabItem.DutyRoster -> TODO()
//                TabItem.Timetable -> TODO()
//            }
//        )
//    }
//
//
//    sealed class TabItem(val title: String) {
//        object Members : TabItem("Участники")
//        object Courses : TabItem("Курсы")
//        object DutyRoster : TabItem("Дежурства")
//        object Timetable : TabItem("Дежурства")
//    }
//}