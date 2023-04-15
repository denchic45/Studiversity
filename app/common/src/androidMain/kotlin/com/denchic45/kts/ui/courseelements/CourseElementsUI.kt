package com.denchic45.kts.ui.courseelements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.kts.R
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseMaterial
import com.denchic45.stuiversity.api.course.element.model.CourseWork
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.util.toString
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


@Composable
fun CourseElementUI(response: CourseElementResponse, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(height = 64.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = { onClick() })
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_assignment),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(response.name, style = MaterialTheme.typography.titleMedium)
            when (val details = response.details) {
                is CourseMaterial -> TODO()
                is CourseWork -> details.dueDate?.let {
                    Text(text = it.toString("dd MMM"), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview
@Composable
fun CourseElementPreview() {
    AppTheme {
        CourseElementUI(
            CourseElementResponse(
                id = UUID.randomUUID(),
                courseId = UUID.randomUUID(),
                name = "Задание №25",
                description = "Сегодня",
                null,
                0,
                CourseWork(LocalDate.now(), LocalTime.now(), CourseWorkType.ASSIGNMENT, 5, null)
            )
        ) {}
    }
}