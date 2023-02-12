package com.stuiversity.util

import com.stuiversity.api.common.Sorting
import io.ktor.client.request.*

fun HttpRequestBuilder.parametersOf(name: String = "sort_by", values: List<Sorting>) = values.forEach {
    parameter(name, it.toString())
}