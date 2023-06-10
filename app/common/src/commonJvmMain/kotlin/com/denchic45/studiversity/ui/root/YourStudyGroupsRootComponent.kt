package com.denchic45.studiversity.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.ui.course.CourseComponent
import com.denchic45.studiversity.ui.courseeditor.CourseEditorComponent
import com.denchic45.studiversity.ui.coursetopics.CourseTopicsComponent
import com.denchic45.studiversity.ui.coursework.CourseWorkComponent
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorComponent
import com.denchic45.studiversity.ui.navigation.ChildrenContainerChild
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootChild
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigatorComponent
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.studygroup.StudyGroupComponent
import com.denchic45.studiversity.ui.yourstudygroups.YourStudyGroupsComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class YourStudyGroupsRootComponent(
    yourStudyGroupsComponent: (
        onCourseOpen: (UUID) -> Unit,
        onStudyGroupOpen: (UUID) -> Unit,
        ComponentContext,
    ) -> YourStudyGroupsComponent,
    studyGroupComponent: (
        onCourseOpen: (UUID) -> Unit,
        onStudyGroupOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> StudyGroupComponent,
//    courseComponent: (
//        onStudyGroupOpen: (UUID) -> Unit,
//        UUID,
//        ComponentContext,
//    ) -> CourseComponent,
    courseEditorComponent: (
        onFinish: () -> Unit,
        UUID?,
        ComponentContext,
    ) -> CourseEditorComponent,
    courseTopicsComponent: (UUID, ComponentContext) -> CourseTopicsComponent,
    courseWorkComponent: (
        onEdit: (courseId: UUID, elementId: UUID?) -> Unit,
        onFinish: () -> Unit,
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkComponent,
    courseWorkEditorComponent: (
        onFinish: () -> Unit,
        courseId: UUID,
        workId: UUID?,
        topicId: UUID?,
        ComponentContext,
    ) -> CourseWorkEditorComponent,
    profileComponent: (onStudyGroupOpen: (UUID) -> Unit, UUID, ComponentContext) -> ProfileComponent,
    rootNavigatorComponent: (initialConfiguration: RootConfig, ComponentContext) -> RootNavigatorComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    RootStackChildrenContainer by rootNavigatorComponent(
        RootConfig.YourStudyGroups,
        componentContext
    ) {

//    override val navigation: StackNavigation<Config> = StackNavigation()
//    override val childStack: Value<ChildStack<Config, Child>> = childStack(
//        source = navigation,
//        initialConfiguration = Config.YourStudyGroups,
//        handleBackButton = true,
//        childFactory = { config, context ->
//            when (config) {
//                Config.YourStudyGroups -> {
//                    Child.YourStudyGroups(
//                        yourStudyGroupsComponent(
//                            { navigation.bringToFront(Config.Course(it)) },
//                            { navigation.bringToFront(Config.StudyGroup(it)) },
//                            context
//                        )
//                    )
//                }
//
//                is Config.StudyGroup -> {
//                    Child.StudyGroup(
//                        studyGroupComponent(
//                            { navigation.bringToFront(Config.Course(it)) },
//                            { navigation.bringToFront(Config.StudyGroup(it)) },
//                            config.studyGroupId,
//                            context
//                        )
//                    )
//                }
//
//                is Config.Course -> {
//                    Child.Course(
//                        courseComponent(
//                            { navigation.bringToFront(Config.StudyGroup(it)) },
//                            config.courseId,
//                            context
//                        )
//                    )
//                }
//
//                is Config.CourseEditor -> {
//                    Child.CourseEditor(
//                        courseEditorComponent(
//                            navigation::pop,
//                            config.courseId,
//                            context
//                        )
//                    )
//                }
//
//                is Config.CourseTopics -> {
//                    Child.CourseTopics(courseTopicsComponent(config.courseId, context))
//                }
//
//                is Config.CourseWork -> {
//                    Child.CourseWork(
//                        courseWorkComponent(
//                            { courseId, workId ->
//                                navigation.push(
//                                    Config.CourseWorkEditor(
//                                        courseId,
//                                        workId
//                                    )
//                                )
//                            },
//                            navigation::pop,
//                            config.courseId,
//                            config.workId,
//                            context
//                        )
//                    )
//                }
//
//                is Config.CourseWorkEditor -> {
//                    Child.CourseWorkEditor(
//                        courseWorkEditorComponent(
//                            navigation::pop,
//                            config.courseId,
//                            config.workId,
//                            null,
//                            context
//                        )
//                    )
//                }
//            }
//        }
//    )

//    private val sidebarNavigation = OverlayNavigation<SidebarConfig>()

//    private val childSidebar = childOverlay(
//        source = sidebarNavigation,
//        childFactory = { config, context ->
//            when (config) {
//                is SidebarConfig.Profile -> {
//                    SidebarChild.Profile(
//                        profileComponent(
//                            { navigation.push(RootConfig.StudyGroup(it)) },
//                            config.userId,
//                            context
//                        )
//                    )
//                }
//            }
//        }
//    )


//    fun onSidebarClose() {
//        sidebarNavigation.dismiss()
//    }

    @Parcelize
    sealed class Config : Parcelable {
        object YourStudyGroups : Config()
        data class StudyGroup(val studyGroupId: UUID) : Config()
        data class Course(val courseId: UUID) : Config()
        data class CourseEditor(val courseId: UUID) : Config()
        data class CourseWork(val courseId: UUID, val workId: UUID) : Config()
        data class CourseWorkEditor(val courseId: UUID, val workId: UUID?) : Config()
        data class CourseTopics(val courseId: UUID) : Config()
    }

    sealed class Child : ChildrenContainerChild {
        class YourStudyGroups(override val component: YourStudyGroupsComponent) : Child()
        class StudyGroup(override val component: StudyGroupComponent) : Child()
        class Course(override val component: CourseComponent) : Child()
        class CourseEditor(override val component: CourseEditorComponent) : Child()
        class CourseWork(override val component: CourseWorkComponent) : Child()
        class CourseWorkEditor(override val component: CourseWorkEditorComponent) : Child()
        class CourseTopics(override val component: CourseTopicsComponent) : Child()
    }

    @Parcelize
    sealed class SidebarConfig : Parcelable {
        data class Profile(val userId: UUID) : SidebarConfig()
    }

    sealed class SidebarChild {
        class Profile(val component: ProfileComponent) : SidebarChild()
    }
}