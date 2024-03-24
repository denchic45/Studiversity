package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.feature.user.model.Avatar
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.source
import java.io.InputStream
import java.util.*

class AvatarService(private val fileSystem: FileSystem) {
    private val avatarsDir = "avatars".toPath()

    fun updateAvatar(userId: UUID, inputStream: InputStream, extension: String) {
        deleteAvatar(userId)
        setAvatar(userId, inputStream, extension)
    }

    fun setAvatar(userId: UUID, inputStream: InputStream, extension: String) {
        val userDir = userDir(userId)
        fileSystem.createDirectory(userDir, mustCreate = false)
        fileSystem.write(userDir / "original.$extension") { writeAll(inputStream.source()) }
    }


    suspend fun resetAvatar(userId: UUID) {
        deleteAvatar(userId)
        generateAvatar(userId)
    }

    private fun deleteAvatar(userId: UUID) {
        fileSystem.deleteRecursively(userDir(userId))
    }

    fun generateAvatar(userId: UUID): String {
//        val newImageBytes = client.get("https://ui-avatars.com/api") {
//            parameter("name", UserDao.findById(userId)!!.firstName[0])
//            parameter("background", "random")
//            parameter("format", "png")
//            parameter("size", 128)
//        }.readBytes()
        return TODO()
//        return setAvatar(userId, CreateFileRequest("avatar.png", newImageBytes), true)
    }

    fun findByUserId(userId: UUID): Avatar {
        val path = avatarsDir / userId.toString()
        val original = fileSystem.list(path).first { it.name.contains("original") }
//        if (fileSystem.exists(path)) todo проверять наличие файла, иначе возвращать стандартное изображение

        return Avatar(
            name = original.name,
            byteArray = fileSystem.read(path, BufferedSource::readByteArray)
        )
    }

    private fun userDir(userId: UUID) = avatarsDir / userId.toString()
}