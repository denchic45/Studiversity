package com.stuiversity.api.course.element.model

sealed interface CourseWorkDetails

data class SingleQuestionWork(val question: String) : CourseWorkDetails