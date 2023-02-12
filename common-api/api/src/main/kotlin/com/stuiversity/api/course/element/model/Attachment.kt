package com.stuiversity.api.course.element.model

sealed interface Attachment

data class FileAttachment(
    val bytes: ByteArray,
    val name: String
) : Attachment {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileAttachment

        if (!bytes.contentEquals(other.bytes)) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}