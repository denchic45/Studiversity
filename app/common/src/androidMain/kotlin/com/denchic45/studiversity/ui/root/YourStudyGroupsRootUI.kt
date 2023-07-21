package com.denchic45.studiversity.ui.root

//@Composable
//fun YourStudyGroupsRootScreen(
//    component: YourStudyGroupsRootComponent,
//) {
//    val appBarState = LocalAppBarState.current
//    val childStack by component.childStack.subscribeAsState()
//
//    Children(childStack) {
//        when (val child = it.instance) {
//            is YourStudyGroupsRootComponent.Child.YourStudyGroups -> {
//                YourStudyGroupsScreen(child.component)
//            }
//
//            is YourStudyGroupsRootComponent.Child.StudyGroup -> {
//                StudyGroupScreen(child.component)
//            }
//
//            is YourStudyGroupsRootComponent.Child.Course -> {
//                CourseScreen(child.component)
//            }
//
//            is YourStudyGroupsRootComponent.Child.CourseEditor -> {
//                CourseEditorScreen(child.component)
//            }
//
//            is YourStudyGroupsRootComponent.Child.CourseTopics -> TODO()
//            is YourStudyGroupsRootComponent.Child.CourseWork -> TODO()
//            is YourStudyGroupsRootComponent.Child.CourseWorkEditor -> TODO()
//        }
//    }
//}