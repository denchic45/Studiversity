package com.denchic45.kts.util


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import io.kamel.core.Resource
import io.kamel.core.config.ResourceConfigBuilder
import io.kamel.core.loadSvgResource
import io.kamel.core.map
import io.kamel.image.config.LocalKamelConfig

@Composable
inline fun lazyPainterResource(
    data: Any,
    key: Any? = data,
    block: ResourceConfigBuilder.() -> Unit = {},
): Resource<Painter> {

    val kamelConfig = LocalKamelConfig.current
    val density = LocalDensity.current
    val resourceConfig = remember(key, density) {
        ResourceConfigBuilder()
            .apply { this.density = density }
            .apply(block)
            .build()
    }

    val painterResource by remember(data, resourceConfig) {
        kamelConfig.loadSvgResource(data, resourceConfig)
    }.collectAsState(Resource.Loading(0F), resourceConfig.coroutineContext)

    return painterResource.map { value -> remember(value) { value } }
}