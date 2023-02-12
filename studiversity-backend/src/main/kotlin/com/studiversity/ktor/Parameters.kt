package com.studiversity.ktor

import com.studiversity.util.tryToUUID
import com.stuiversity.api.common.Sorting
import com.stuiversity.api.common.SortingClass
import com.stuiversity.api.course.element.model.SortingCourseElements
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import java.util.*
import kotlin.reflect.typeOf

fun Parameters.getUuid(name: String): UUID = try {
    getOrFail(name).tryToUUID()
} catch (e: MissingRequestParameterException) {
    throw e
}

inline fun <reified T : Sorting> Parameters.getSortingBy(
    sortingClass: SortingClass<T>,
    parameterName: String = "sort_by"
): List<T>? = getAll(parameterName)?.map {
    sortingClass.create(it) ?: throw ParameterConversionException(it, typeOf<SortingCourseElements>().toString())
}