package com.denchic45.kts.data.service

import com.denchic45.kts.ApiKeys
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import me.tatarka.inject.annotations.Inject
import org.koin.core.parameter.parametersOf

@Inject
class AvatarService(private val client: HttpClient) {

    suspend fun generateAvatar(name: String) = client
        .get("https://avatars.abstractapi.com/v1/") {
            parametersOf(
                "api_key" to ApiKeys.avatarsApiKey,
                "name" to name[0],
                "image_size" to 192
            )
        }.readBytes()

}