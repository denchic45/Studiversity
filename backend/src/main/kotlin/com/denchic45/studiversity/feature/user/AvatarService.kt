package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.connector
import com.denchic45.studiversity.feature.user.model.Avatar
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.util.*
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.source
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.min


class AvatarService(private val fileSystem: FileSystem) {
    private val avatarsDir = "data/avatars".toPath()

    fun updateAvatar(userId: UUID, inputStream: InputStream, extension: String) {
        deleteAvatar(userId)
        setAvatar(userId, inputStream, extension)
    }

    fun setAvatar(userId: UUID, inputStream: InputStream, extension: String) {
        val userDir = userDir(userId)
        fileSystem.createDirectory(userDir, mustCreate = false)
        fileSystem.write(userDir / "original.$extension") { writeAll(inputStream.source()) }
    }


    fun resetAvatar(userId: UUID, name: String) {
        deleteAvatar(userId)
        generateAvatar(userId, name)
    }

    fun deleteAvatar(userId: UUID) {
        fileSystem.deleteRecursively(userDir(userId))
    }

    fun generateAvatar(userId: UUID, name: String): String {
//        val newImageBytes = client.get("https://ui-avatars.com/api") {
//            parameter("name", UserDao.findById(userId)!!.firstName[0])
//            parameter("background", "random")
//            parameter("format", "png")
//            parameter("size", 128)
//        }.readBytes()
        return generate(userId, name)
//        return setAvatar(userId, CreateFileRequest("avatar.png", newImageBytes), true)
    }

    @Throws(IOException::class)
    fun generate(
        userId: UUID,
        name: String
    ): String {
        val width = 512
        val height = 512
        val initial = name.substring(0, 1).uppercase(Locale.getDefault())

        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g2 = img.createGraphics()

        // Rectangle of random color
        val rectangle = Rectangle2D.Double(0.0, 0.0, width.toDouble(), height.toDouble())
        g2.paint = Color.BLUE
        g2.fill(rectangle)

        // Antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Font scale
        val fontSize = Math.round((min(width.toDouble(), height.toDouble()) * 4f / 5f))
            .toInt()
        val font = Font(Font.SANS_SERIF, Font.PLAIN, fontSize)
        g2.font = font
        val fm = g2.fontMetrics
        val fontSizeLast = width / fm.stringWidth(initial).toFloat() * fontSize
        font.deriveFont(fontSizeLast)

        // Font color
        g2.color = Color.WHITE

        // Center font
        val fontRect = fm.getStringBounds(initial, g2)
        val x = (width - fontRect.width.toInt()) / 2
        val y = (height - fontRect.height.toInt()) / 2 + fm.ascent

        // Draw font
        g2.drawString(initial, x, y)
        val path = userDir(userId)
        fileSystem.createDirectories(path)
        ImageIO.write(img, "PNG", (path / "original.png").toFile())
        return url {
            host = connector.host
            port = connector.port
            path("avatars", userId.toString())
        }
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