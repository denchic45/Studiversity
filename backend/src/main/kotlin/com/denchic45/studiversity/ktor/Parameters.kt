package com.denchic45.studiversity.ktor

import com.denchic45.studiversity.util.tryToUUID
import com.denchic45.stuiversity.api.common.Sorting
import com.denchic45.stuiversity.api.common.SortingClass
import com.denchic45.stuiversity.api.course.element.model.CourseElementsSorting
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import java.util.*
import kotlin.reflect.typeOf

fun Parameters.getUuid(name: String): UUID? = try {
    get(name)?.tryToUUID()
} catch (e: MissingRequestParameterException) {
    throw e
}

fun Parameters.getUuidOrFail(name: String): UUID = try {
    getOrFail(name).tryToUUID()
} catch (e: MissingRequestParameterException) {
    throw e
}

inline fun <reified T : Enum<T>> Parameters.getEnum(name: String): T? {
    return get(name)?.let<String, T>(::enumValueOf)
}

inline fun <reified T : Enum<T>> Parameters.getEnumOrFail(name: String): T {
    return enumValueOf(getOrFail(name))
}


inline fun <reified T : Sorting> Parameters.getSortingBy(
    sortingClass: SortingClass<T>,
    parameterName: String = "sort_by"
): List<T>? = getAll(parameterName)?.map {
    sortingClass.create(it) ?: throw ParameterConversionException(it, typeOf<T>().toString())
}



// TODO: Add filter parameters

//inline fun  <reified T : FieldFilter<*>> Parameters.getFieldFiltersBy(
//    fieldFilterClass: FieldFilterClass<T>
//) {
//    fieldFilterClass.create()
//}