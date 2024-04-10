package com.denchic45.studiversity.ui.courseelements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun CourseElementsScreen(component: CourseElementsComponent) {
    val elementsResource by component.elements.collectAsState()

    CourseElementsContent(
        elementsResource = elementsResource,
        onElementClick = component::onItemClick,
    )
}