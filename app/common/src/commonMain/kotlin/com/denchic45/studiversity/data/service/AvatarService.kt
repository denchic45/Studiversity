package com.denchic45.studiversity.data.service

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import me.tatarka.inject.annotations.Inject

@Inject
class AvatarService @javax.inject.Inject constructor(private val client: HttpClient) {
    val colors = listOf(
        "3473E1",
        "FCBF49",
        "663DC9",
        "34A853",
        "DD5252",
        "EFC777"
    )

    suspend fun generateAvatar(name: String, color: String = colors.random()) = client
        .get("https://avatars.abstractapi.com/v1/") {
//            parameter("api_key", ApiKeys.avatarsApiKey)
            parameter("name", name)
            parameter("background_color", color)
            parameter("image_size", 192)
            parameter("char_limit", 1)
        }.readBytes()
}