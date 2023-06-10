package com.denchic45.studiversity.ui.studygroups

//@Inject
//class StudyGroupsComponent(
//    private val findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
//    private val overlayNavigator: OverlayNavigation<OverlayConfig>,
//    private val yourStudyGroupsRootComponent: (OverlayNavigation<OverlayConfig>, groupId: UUID, ComponentContext) -> YourStudyGroupsRootComponent,
//    @Assisted
//    componentContext: ComponentContext,
//) : ComponentContext by componentContext {
//
//
//    private val groups = flow { emit(findYourStudyGroupsUseCase()) }
//    private val navigation = StackNavigation<StudyGroupsConfig>()
//
//    val stack = childStack(
//        source = navigation,
//        initialConfiguration = StudyGroupsConfig.Empty,
//        childFactory = { config, _ ->
//            when (config) {
//                StudyGroupsConfig.Empty -> StudyGroupsChild.Empty
//                is StudyGroupsConfig.Group -> {
//                    StudyGroupsChild.Group(
//                        yourStudyGroupsRootComponent(
//                            overlayNavigator,
//                            config.studyGroupId,
//                            componentContext
//                        )
//                    )
//                }
//            }
//        })
//}
//
//
//sealed class StudyGroupsConfig : Parcelable {
//    @Parcelize
//    object Empty : StudyGroupsConfig()
//
//    @Parcelize
//    data class Group(val studyGroupId: UUID) : StudyGroupsConfig()
//}
//
//sealed class StudyGroupsChild {
//    object Empty : StudyGroupsChild()
//    data class Group(val component: YourStudyGroupsRootComponent) : StudyGroupsChild()
//}