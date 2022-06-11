package com.bulanovdm.pitermonitor

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class MailService(private val javaMailSender: JavaMailSender, private val mailProperties: MailProperties) {

    fun send(mailMessage: SimpleMailMessage) {
        mailMessage.setFrom(mailProperties.username)
        javaMailSender.send(mailMessage)
    }
}

@Component
class BookMailService(private val mailService: MailService, private val mailProperties: MailProperties) {

    fun sendChangedBooks(books: List<Book>, booksWas: List<Book>) {
        val emailText = "Next books was/were updated: \n ${books.joinToString(separator = "\n") { "$it \n" }}"
        val emailTextWas = "Was: \n ${booksWas.joinToString(separator = "\n") { "$it \n" }}"
        sendMessage(emailText + emailTextWas, mailProperties.recipient)
    }

    private fun sendMessage(emailText: String, recipientEmail: String) {
        val message = SimpleMailMessage()
        message.setSubject("Books updated")
        message.setText(emailText)
        message.setTo(recipientEmail)
        message.setFrom("sandbox.mgsend.net")
        mailService.send(message)
    }
}

