package com.denchic45.kts.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.RootComponent
import com.denchic45.kts.ui.course.CourseComponent
import com.denchic45.kts.ui.courseeditor.CourseEditorComponent
import com.denchic45.kts.ui.coursetopics.CourseTopicsComponent
import com.denchic45.kts.ui.coursework.CourseWorkComponent
import com.denchic45.kts.ui.courseworkeditor.CourseWorkEditorComponent
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.ui.yourstudygroups.YourStudyGroupsComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class YourStudyGroupsRootComponent(
    yourStudyGroupsComponent: (
        onCourseOpen: (UUID) -> Unit,
        ComponentContext
    ) -> YourStudyGroupsComponent,
    courseComponent: (
        UUID,
        onCourseEditorOpen: (courseId: UUID) -> Unit,
        onWorkOpen: (courseId: UUID, elementId: UUID) -> Unit,
        onWorkEditorOpen: (courseId: UUID, elementId: UUID?) -> Unit,
        onCourseTopicsOpen: (courseId: UUID) -> Unit,
        onMemberOpen: (memberId: UUID) -> Unit,
        ComponentContext
    ) -> CourseComponent,
    courseEditorComponent: (
        onFinish: () -> Unit,
        UUID?,
        ComponentContext
    ) -> CourseEditorComponent,
    courseTopicsComponent: (UUID, ComponentContext) -> CourseTopicsComponent,
    courseWorkComponent: (
        onEdit: (courseId: UUID, elementId: UUID?) -> Unit,
        onFinish: () -> Unit,
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkComponent,
    courseWorkEditorComponent: (UUID, UUID?, ComponentContext) -> CourseWorkEditorComponent,

    profileComponent: (UUID, ComponentContext) -> ProfileComponent,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext,
    RootComponent<YourStudyGroupsRootComponent.Config, YourStudyGroupsRootComponent.Child> {

    override val navigation: StackNavigation<Config> = StackNavigation()
    override val childStack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.YourStudyGroups,
        childFactory = { config, context ->
            when (config) {
                Config.YourStudyGroups -> {
                    Child.YourStudyGroups(
                        yourStudyGroupsComponent(
                            { navigation.push(Config.Course(it)) },
                            context
                        )
                    )
                }

                is Config.Course -> {
                    Child.Course(
                        courseComponent(
                            config.courseId,
                            { navigation.push(Config.CourseEditor(it)) },
                            { courseId, workId ->
                                navigation.push(Config.CourseWork(courseId, workId))
                            },
                            { courseId, workId ->
                                navigation.push(
                                    Config.CourseWorkEditor(
                                        courseId,
                                        workId
                                    )
                                )
                            },
                            { navigation.push(Config.CourseTopics(it)) },
                            { sidebarNavigation.activate(SidebarConfig.Profile(it)) },
                            context
                        )
                    )
                }

                is Config.CourseEditor -> {
                    Child.CourseEditor(
                        courseEditorComponent(
                            navigation::pop,
                            config.courseId,
                            context
                        )
                    )
                }

                is Config.CourseTopics -> {
                    Child.CourseTopics(courseTopicsComponent(config.courseId, context))
                }

                is Config.CourseWork -> {
                    Child.CourseWork(
                        courseWorkComponent(
                            { courseId, workId ->
                                navigation.push(
                                    Config.CourseWorkEditor(
                                        courseId,
                                        workId
                                    )
                                )
                            },
                            navigation::pop,
                            config.courseId,
                            config.workId,
                            context
                        )
                    )
                }

                is Config.CourseWorkEditor -> {
                    Child.CourseWorkEditor(
                        courseWorkEditorComponent(config.courseId, config.workId, context)
                    )
                }
            }
        }
    )

    private val sidebarNavigation = OverlayNavigation<SidebarConfig>()

    private val childSidebar = childOverlay(
        source = sidebarNavigation,
        childFactory = { config, context ->
            when (config) {
                is SidebarConfig.Profile -> {
                    SidebarChild.Profile(profileComponent(config.userId, context))
                }
            }
        }
    )


    fun onSidebarClose() {
        sidebarNavigation.dismiss()
    }

    @Parcelize
    sealed class Config : Parcelable {
        object YourStudyGroups : Config()
        data class Course(val courseId: UUID) : Config()
        data class CourseEditor(val courseId: UUID) : Config()
        data class CourseWork(val courseId: UUID, val workId: UUID) : Config()
        data class CourseWorkEditor(val courseId: UUID, val workId: UUID?) : Config()
        data class CourseTopics(val courseId: UUID) : Config()
    }

    sealed class Child {
        class YourStudyGroups(val component: YourStudyGroupsComponent) : Child()
        class Course(val component: CourseComponent) : Child()
        class CourseEditor(val component: CourseEditorComponent) : Child()
        class CourseWork(val component: CourseWorkComponent) : Child()
        class CourseWorkEditor(val component: CourseWorkEditorComponent) : Child()
        class CourseTopics(val component: CourseTopicsComponent) : Child()
    }

    @Parcelize
    sealed class SidebarConfig : Parcelable {
        data class Profile(val userId: UUID) : SidebarConfig()
    }

    sealed class SidebarChild {
        class Profile(val component: ProfileComponent) : SidebarChild()
    }
}