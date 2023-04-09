package com.denchic45.kts.ui

import android.content.Context
import coil.ImageLoader
import coil.decode.SvgDecoder
import kotlin.properties.Delegates

var imageLoader : ImageLoader by Delegates.notNull()
    private set

fun initImageLoader(context: Context) {
    imageLoader = ImageLoader.Builder(context)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()
}