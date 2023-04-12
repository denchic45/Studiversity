package com.denchic45.kts.ui


sealed class UiImage {

    data class Url(val value: String) : UiImage()

    data class IdImage(val value: Int) : UiImage()
}

fun UiImage.onName(fn: (value: String) -> Unit): UiImage =
    this.apply { if (this is UiImage.Url) fn(value) }

fun UiImage.onId(fn: (value: Int) -> Unit): UiImage =
    this.apply { if (this is UiImage.IdImage) fn(value) }