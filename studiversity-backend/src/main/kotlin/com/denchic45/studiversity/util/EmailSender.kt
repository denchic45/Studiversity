package com.denchic45.studiversity.util

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail

class EmailSender(
    private val host: String,
    private val port: Int,
    private val ssl: Boolean,
    private val userName: String,
    private val password: String = "qpiebzputrqpxydm",
) {
    fun sendSimpleEmail(emailAddress: String, emailSubject: String, message: String) {
        try {
            SimpleEmail().apply {
                hostName = host
                setSmtpPort(port)
                setAuthenticator(DefaultAuthenticator(userName, password))
                isSSLOnConnect = ssl
                setFrom(userName)
                subject = emailSubject
                setMsg(message)
                addTo(emailAddress)
            }.send()
        } catch (ex: Exception) {
            println("Unable to send email")
            println(ex)
        }
    }
}