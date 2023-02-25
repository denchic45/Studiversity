package com.denchic45.stuiversity.util

import com.denchic45.stuiversity.api.common.Sorting
import io.ktor.client.request.*

fun HttpRequestBuilder.parametersOf(name: String = "sort_by", values: List<Sorting>) = values.forEach {
    parameter(name, it.toString())
}

//fun <T> HttpRequestBuilder.parameter(fieldFilter: FieldFilter<T>) {
//    parameter(fieldFilter.field, fieldFilter.value)
//}