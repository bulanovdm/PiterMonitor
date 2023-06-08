package com.bulanovdm.pitermonitor.service

import com.bulanovdm.pitermonitor.conf.MailProperties
import com.bulanovdm.pitermonitor.model.Book
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class MailService(private val javaMailSender: JavaMailSender) {

    internal fun send(mailMessage: SimpleMailMessage) {
        javaMailSender.send(mailMessage)
    }
}

@Component
class BookMailService(private val mailService: MailService, private val mailProperties: MailProperties) {

    fun sendChangedBooks(books: Set<Book>, booksWas: Set<Book>, bookDiscount: Set<Book>) {
        val emailText = "Следующие книги были обновлены: \n ${books.joinToString(separator = "\n") { "$it \n" }}"
        val emailTextWas = "Было раньше: \n ${booksWas.joinToString(separator = "\n") { "$it \n" }}"
        val emailBookDiscount = "Дисконт сейчас: \n ${bookDiscount.joinToString(separator = "\n") { "$it \n" }}"
        sendMessage(emailText + emailTextWas + emailBookDiscount, mailProperties.recipient)
    }

    private fun sendMessage(emailText: String, recipientEmail: String) {
        val message = SimpleMailMessage()
        message.subject = "Книги обновлены"
        message.text = emailText
        message.setTo(recipientEmail)
        message.from = mailProperties.username
        mailService.send(message)
    }
}

